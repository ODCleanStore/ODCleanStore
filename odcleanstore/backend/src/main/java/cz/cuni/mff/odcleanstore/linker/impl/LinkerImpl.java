package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.ObjectIdentificationConfig;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import cz.cuni.mff.odcleanstore.core.SerializationLanguage;
import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.data.GraphLoaderUtils;
import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.shared.util.RDFPrefixesLoader;
import cz.cuni.mff.odcleanstore.shared.util.RDFprefix;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import de.fuberlin.wiwiss.silk.Silk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of the {link #Linker} interface.
 *
 * @author Tomas Soukup
 */
public class LinkerImpl implements Linker {
	private static final Logger LOG = LoggerFactory.getLogger(LinkerImpl.class);

	private static final String DEBUG_INPUT_FILENAME = "debugInput.xml";
	private static final String DEBUG_OUTPUT_FILENAME = "debugResult.xml";

	private static final String CONFIG_XML_CELL = "Cell";
	private static final String CONFIG_XML_ENTITY1 = "entity1";
	private static final String CONFIG_XML_ENTITY2 = "entity2";
	private static final String CONFIG_XML_RESOURCE = "rdf:resource";
	private static final String CONFIG_XML_MEASURE = "measure";

	private static final String LINK_WITHIN_GRAPH_KEY = "linkWithinGraph";
	private static final String LINK_ATTACHED_GRAPHS_KEY = "linkAttachedGraphs";

	private boolean isFirstInPipeline;
	private ObjectIdentificationConfig globalConfig;
	private Integer[] groupIds;

	/**
	 * Constructor.
	 *
	 * @param isFirstInPipeline flag, when set to true, linker clears existing links before creating new
	 * when transforming existing graph
	 * @param groupIds list of linkage rules groups IDs, which are used transformGraph and debugRules methods
	 */
	public LinkerImpl(boolean isFirstInPipeline, Integer... groupIds) {
		this.isFirstInPipeline = isFirstInPipeline;
		this.globalConfig = ConfigLoader.getConfig().getObjectIdentificationConfig();
		this.groupIds = groupIds;
	}

	 /**
     * {@inheritDoc}
     *
     * Generates links between input graph in dirty database and graphs in clean database.
     *
     * Obtains linkage rule-groups from transformer configuration.
     * When no groups are specified, uses configuration files from designated directory.
     *
     * @param inputGraph {@inheritDoc}
     * @param context {@inheritDoc}
     */
	@Override
	public void transformGraph(TransformedGraph inputGraph, TransformationContext context)
			throws TransformerException {
		String cleanGraphName = null;
        if (context.getTransformationType() == EnumTransformationType.EXISTING) {
            LOG.info("Linking existing graph: {}", inputGraph.getGraphName());
            try {
	            cleanGraphName = createCleanGraphGroup(context, inputGraph);
	            if (isFirstInPipeline) {
			        LinkerDao dao = LinkerDao.getInstance(context.getCleanDatabaseCredentials(),
			        		context.getDirtyDatabaseCredentials());
			        dao.clearGraph(getLinksGraphId(inputGraph));
	            }
            } catch (DatabaseException e) {
	            throw new TransformerException(e);
	        }
        } else {
            LOG.info("Linking new graph: {}", inputGraph.getGraphName());
        }

		File configFile = null;
		try {
			List<SilkRule> rules = loadRules(context);
			if (rules.isEmpty()) {
			    LOG.info("Nothing to link.");
			} else {
    			List<RDFprefix> prefixes = RDFPrefixesLoader.loadPrefixes(
    					context.getCleanDatabaseCredentials());

    			Properties transformerProperties = parseProperties(context.getTransformerConfiguration());
    			boolean linkWithinGraph = isLinkWithinGraph(transformerProperties);
    			boolean linkAttachedGraphs = isLinkAttachedGraphs(transformerProperties);

    			String dirtyGraphName = inputGraph.getGraphName();
    			if (linkAttachedGraphs) {
    				dirtyGraphName = createDirtyGraphGroup(context, inputGraph);
    			}

    			inputGraph.addAttachedGraph(getLinksGraphId(inputGraph));

    			for (SilkRule rule: rules) {
    				LOG.info("Creating link configuration file for rule: {}", rule.toString());
    				configFile = ConfigBuilder.createLinkConfigFile(rule, prefixes, inputGraph.getGraphId(),
    						cleanGraphName, dirtyGraphName, context, globalConfig, linkWithinGraph);
        			LOG.info("Calling Silk with temporary configuration file: {}", configFile.getAbsolutePath());
        			Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
        			LOG.info("Linking by one rule finished.");
    			}

    			if (linkAttachedGraphs) {
    				deleteGraphGroup(context, dirtyGraphName, EnumDatabaseInstance.DIRTY);
    			}
    			if (context.getTransformationType() == EnumTransformationType.EXISTING) {
    				deleteGraphGroup(context, cleanGraphName, EnumDatabaseInstance.CLEAN);
    			}
    			LOG.info("Linking by all rules finished.");
			}
		} catch (DatabaseException e) {
			throw new TransformerException(e);
		} catch (TransformedGraphException e) {
			throw new TransformerException(e);
		} finally {
			if (configFile != null) {
				configFile.delete();
			}
		}
	}

	@Override
    public void shutdown() {
    }

	/**
	 * Loads list of rules from database.
	 *
	 * Parses transformer configuration to obtain rule groups.
	 * Then loads rules from these groups from DB using {@link LinkerDao}
	 *
	 * @param transformerConfiguration string containing the list of rule-groups IDs
	 * @param dao is used to load rules from DB
	 */
	private List<SilkRule> loadRules(TransformationContext context, TableVersion tableVersion)
			throws ConnectionException, QueryException {
		LOG.info("Loading rule groups: {}", groupIds);
		LinkerDao dao = LinkerDao.getInstance(context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials());
		return dao.loadRules(groupIds, tableVersion);
	}

	private List<SilkRule> loadRules(TransformationContext context)
			throws ConnectionException, QueryException {
		return loadRules(context, TableVersion.COMMITTED);
	}

	private String getLinksGraphId(TransformedGraph inputGraph) {
		return ODCSInternal.GENERATED_LINKS_GRAPH_URI_PREFIX + inputGraph.getGraphId();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.odcleanstore.linker.Linker#debugRules(java.lang.String, cz.cuni.mff.odcleanstore.transformer.TransformationContext, cz.cuni.mff.odcleanstore.data.TableVersion)
	 */
	@Override
    public List<DebugResult> debugRules(String input, TransformationContext context, TableVersion tableVersion)
			throws TransformerException {
		return debugRules(stringToFile(input, context.getTransformerDirectory()), context, tableVersion,
				GraphLoaderUtils.guessLanguage(input));
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.odcleanstore.linker.Linker#debugRules(java.io.File, cz.cuni.mff.odcleanstore.transformer.TransformationContext, cz.cuni.mff.odcleanstore.data.TableVersion, cz.cuni.mff.odcleanstore.shared.SerializationLanguage)
	 */
	@Override
    public List<DebugResult> debugRules(File inputFile, TransformationContext context, TableVersion tableVersion,
    		SerializationLanguage language) throws TransformerException {
		List<DebugResult> resultList = new ArrayList<DebugResult>();
		File configFile = null;
		String resultFileName = null;
		String resultFileNameWithin = null;
		try {
			List<SilkRule> rules = loadRules(context, tableVersion);
			List<RDFprefix> prefixes = RDFPrefixesLoader.loadPrefixes(context.getCleanDatabaseCredentials());
			for (SilkRule rule: rules) {
				resultFileName = createFileName(
						rule.getId().toString(), context.getTransformerDirectory(), DEBUG_OUTPUT_FILENAME);
				resultFileNameWithin = ConfigBuilder.updateFilenameWithin(resultFileName);
				configFile = ConfigBuilder.createDebugLinkConfigFile(rule, prefixes, context, globalConfig,
						inputFile.getAbsolutePath(), resultFileName, language);
				Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
				List<LinkedPair> linkedPairs = parseLinkedPairs(resultFileName);
				loadLabels(inputFile, linkedPairs, language);
				loadLabels(context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials(), linkedPairs);
				resultList.add(new DebugResult(rule.getLabel(), linkedPairs));
				deleteFile(configFile);
				configFile = null;
				deleteFile(resultFileName);
				resultFileName = null;

				if (globalConfig.isLinkWithinGraph()) {
					linkedPairs = parseLinkedPairs(resultFileNameWithin);
					loadLabels(inputFile, linkedPairs, language);
					loadLabels(context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials(), linkedPairs);
					resultList.add(new DebugResult(rule.getLabel() + " - links within input", linkedPairs));
					deleteFile(resultFileNameWithin);
					resultFileNameWithin = null;
				}
			}

		} catch (Exception e) {
			throw new TransformerException(e);
		} finally {
			deleteFile(inputFile);
			if (configFile != null) {
				deleteFile(configFile);
			}
			if (resultFileName != null) {
				deleteFile(resultFileName);
			}
			if (resultFileNameWithin != null) {
				deleteFile(resultFileNameWithin);
			}
		}

		return resultList;
	}

	private String createFileName(String ruleId, File transformerDirectory, String fileName) {
		return new File(transformerDirectory,
				ruleId + UUID.randomUUID().toString() + fileName).getAbsolutePath();
	}

	private List<LinkedPair> parseLinkedPairs(String resultFileName) throws TransformerException {
		List<LinkedPair> pairList = new ArrayList<LinkedPair>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(resultFileName);
			NodeList cells = doc.getElementsByTagName(CONFIG_XML_CELL);
			int cellLength = cells.getLength();

			for (int i = 0; i < cellLength; i++) {
				String firstUri = null;
				String secondUri = null;
				Double confidence = null;
				Element cell = (Element)cells.item(i);
				NodeList cellChildern = cell.getChildNodes();
				int childLength = cellChildern.getLength();

				for (int j = 0; j < childLength; j++) {
					org.w3c.dom.Node child = cellChildern.item(j);
					String childName = child.getNodeName();
					if (CONFIG_XML_ENTITY1.equals(childName)) {
						Element element = (Element)child;
						firstUri = element.getAttribute(CONFIG_XML_RESOURCE);
					} else if (CONFIG_XML_ENTITY2.equals(childName)) {
						Element element = (Element)child;
						secondUri = element.getAttribute(CONFIG_XML_RESOURCE);
					} else if (CONFIG_XML_MEASURE.equals(childName)) {
						String content = child.getTextContent();
						int leftIndex = content.indexOf('(');
						if (leftIndex != -1) {
							int rightIndex = content.indexOf(')');
							if (rightIndex == -1) {
								rightIndex = content.length();
							}
							content = content.substring(leftIndex + 1, rightIndex);
						}
						confidence = Double.valueOf(content);
					}
				}

				pairList.add(new LinkedPair(firstUri, secondUri, confidence));
			}

		} catch (Exception e) {
			throw new TransformerException(e);
		}

		return pairList;
	}

	private File stringToFile(String input, File targetDirectory) throws TransformerException {
		File file = new File(createFileName("", targetDirectory, DEBUG_INPUT_FILENAME));
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file, ODCSUtils.DEFAULT_ENCODING);
			writer.write(input);
		} catch (FileNotFoundException e) {
			throw new TransformerException(e);
		} catch (UnsupportedEncodingException e) {
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return file;
	}

    private void loadLabels(File inputFile, List<LinkedPair> linkedPairs, SerializationLanguage language)
            throws TransformerException {
        Repository repository = null;
        RepositoryConnection connection = null;
        try {
            repository = loadRepositoryFromFile(inputFile, language);
            connection = repository.getConnection();

            for (LinkedPair pair : linkedPairs) {
                URI firstURI = repository.getValueFactory().createURI(pair.getFirstUri());
                URI secondURI = repository.getValueFactory().createURI(pair.getSecondUri());
                RepositoryResult<Statement> it = connection.getStatements(firstURI, org.openrdf.model.vocabulary.RDFS.LABEL,
                        null, false);
                if (it.hasNext()) {
                    pair.setFirstLabel(it.next().getObject().stringValue());
                }

                it = connection.getStatements(secondURI, org.openrdf.model.vocabulary.RDFS.LABEL, null, false);
                if (it.hasNext()) {
                    pair.setSecondLabel(it.next().getObject().stringValue());
                }
            }
        } catch (RepositoryException e) {
            throw new TransformerException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (repository != null) {
                    repository.shutDown();
                }
            } catch (RepositoryException e) {
                // ignore
            }
        }
    }

    private Repository loadRepositoryFromFile(File inputFile, SerializationLanguage language)
            throws TransformerException {
        Repository repository = new SailRepository(new MemoryStore());
        try {
            repository.initialize();
            RepositoryConnection connection = repository.getConnection();
            RDFFormat format = (language == SerializationLanguage.RDFXML) ? RDFFormat.RDFXML : RDFFormat.N3;
            try {
                connection.add(inputFile, "", format);
            } finally {
                connection.close();
            }
        } catch (OpenRDFException e) {
            throw new TransformerException(e);
        } catch (java.io.IOException e) {
            throw new TransformerException(e);
        }
        return repository;
    }

    private void loadLabels(JDBCConnectionCredentials cleanDBCredentials, JDBCConnectionCredentials dirtyDBCredentials,
		List<LinkedPair> linkedPairs) throws TransformerException {
		Map<String, String> uriLabelMap = createUriLabelMap(linkedPairs);
		LinkerDao dao;
		try {
			dao = LinkerDao.getInstance(cleanDBCredentials, dirtyDBCredentials);
			dao.loadLabels(uriLabelMap);
			for (LinkedPair pair: linkedPairs) {
				String label = uriLabelMap.get(pair.getFirstUri());
				if (label != null) {
					pair.setFirstLabel(label);
				}
				label = uriLabelMap.get(pair.getSecondUri());
				if (label != null) {
					pair.setSecondLabel(label);
				}
			}
		} catch (ConnectionException e) {
			throw new TransformerException(e);
		} catch (QueryException e) {
			throw new TransformerException(e);
		}
	}

	private Map<String, String> createUriLabelMap(List<LinkedPair> linkedPairs) {
		Map<String, String> uriLabelMap = new HashMap<String, String>();
		for (LinkedPair pair: linkedPairs) {
			uriLabelMap.put(pair.getFirstUri(), pair.getFirstLabel());
			uriLabelMap.put(pair.getSecondUri(), pair.getSecondLabel());
		}
		return uriLabelMap;
	}

	private void deleteFile(String fileName) {
		File file = new File(fileName);
		deleteFile(file);
	}

	private void deleteFile(File file) {
		if (!file.delete()) {
			LOG.warn("Failed to delete file {}", file.getAbsolutePath());
		}
	}

	private Properties parseProperties(String input) {
		Properties properties = new Properties();
		try {
			properties.load(new StringReader(input));
		} catch (IOException e) {
			LOG.warn("Failed to parse properties from transformerConfiguration.");
		}
		return properties;
	}

	private boolean isLinkWithinGraph(Properties properties) {
		String property = (String)properties.get(LINK_WITHIN_GRAPH_KEY);
		if (property != null) {
			return Boolean.parseBoolean(property);
		} else {
			return globalConfig.isLinkWithinGraph();
		}
	}

	private boolean isLinkAttachedGraphs(Properties properties) {
		String property = (String)properties.get(LINK_ATTACHED_GRAPHS_KEY);
		if (property != null) {
			return Boolean.parseBoolean(property);
		} else {
			return globalConfig.isLinkAttachedGraphs();
		}
	}

	private String createDirtyGraphGroup(TransformationContext context, TransformedGraph graph)
			throws ConnectionException, QueryException {
		String groupName = graph.getGraphName() + "WithAttached";
		LinkerDao dao = LinkerDao.getInstance(
				context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials());

		LOG.info("Deleting temporary graph group with attached graphs in dirty DB (if it extists): {}", groupName);
		dao.deleteGraphGroup(groupName, EnumDatabaseInstance.DIRTY, true);

		LOG.info("Creating temporary graph group with attached graphs in dirty DB: {}", groupName);
		Set<String> graphNames = new HashSet<String>(graph.getAttachedGraphNames());
		graphNames.add(graph.getGraphName());
		dao.createGraphGroup(groupName, graphNames, EnumDatabaseInstance.DIRTY);
		return groupName;
	}

	private void deleteGraphGroup(TransformationContext context, String groupName, EnumDatabaseInstance db)
			throws ConnectionException, QueryException {
		LOG.info("Deleting temporary graph group from {} DB: {}", db.toString(), groupName);
		LinkerDao dao = LinkerDao.getInstance(
				context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials());
		dao.deleteGraphGroup(groupName, db, false);
	}

	private String createCleanGraphGroup(TransformationContext context, TransformedGraph graph)
			throws ConnectionException, QueryException {
		String groupName = graph.getGraphName() + "Group";
		LinkerDao dao = LinkerDao.getInstance(
				context.getCleanDatabaseCredentials(), context.getDirtyDatabaseCredentials());

		LOG.info("Deleting temporary graph group without processed graph in clean DB (if it exists): {}", groupName);
		dao.deleteGraphGroup(groupName, EnumDatabaseInstance.CLEAN, true);

		LOG.info("Creating temporary graph group without processed graph in clean DB: {}", groupName);
		Set<String> graphNames = dao.getAllGraphNames();
		graphNames.removeAll(graph.getAttachedGraphNames());
		graphNames.remove(graph.getGraphName());
		dao.createGraphGroup(groupName, graphNames, EnumDatabaseInstance.CLEAN);
		return groupName;
	}
}
