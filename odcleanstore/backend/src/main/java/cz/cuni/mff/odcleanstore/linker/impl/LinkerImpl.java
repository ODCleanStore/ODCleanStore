package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.shared.RDFPrefixesLoader;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import de.fuberlin.wiwiss.silk.Silk;

/**
 * Default implementation of the {link #Linker} interface.
 * 
 * @author Tomas Soukup
 */
public class LinkerImpl implements Linker {
	private static final Logger LOG = LoggerFactory.getLogger(LinkerImpl.class);
	/** 
	 * URI of graph to store generated links to 
	 */
	private static final String LINKS_GRAPH_NAME = "http://odcs.mff.cuni.cz/namedGraph/generatedLinks/";
	
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
	public void transformNewGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
		LOG.info("Linking new graph: {}", inputGraph.getGraphName());
		String config = context.getTransformerConfiguration();
		if (config == null || config.isEmpty()) {
			LOG.info("No configuration specified, using XML files in directory {}", context.getTransformerDirectory().getAbsolutePath());
			linkByConfigFiles(context);
		} else {
			LinkerDao dao;
			File configFile = null;
			try {
				// TODO get SparqlEndpoint AND JDBC connection
				dao = LinkerDao.getInstance(context.getCleanDatabaseEndpoint());
				List<String> rawRules = loadRules(context.getTransformerConfiguration(), dao);
				List<RDFprefix> prefixes = RDFPrefixesLoader.loadPrefixes(context.getCleanDatabaseEndpoint());
				
				String linksGraphName = LINKS_GRAPH_NAME + inputGraph.getGraphId();
				configFile = ConfigBuilder.createLinkConfigFile(rawRules, prefixes, inputGraph, context, linksGraphName);
				
				inputGraph.addAttachedGraph(linksGraphName);
				
				LOG.info("Calling Silk with temporary configuration file: {}", configFile.getAbsolutePath());
				Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
				LOG.info("Linking finished.");
				
				configFile.delete();
			} catch (SQLException e) {
				throw new TransformerException(e);
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
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @param inputGraph {@inheritDoc}
	 * @param context {@inheritDoc}
	 */
	@Override
	public void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context) {
		// TODO Auto-generated method stub		
	}
	
	@Override
    public void shutdown() {
    }
	
	/**
	 * {@inheritDoc}
	 *
	 * @param context {@inheritDoc}
	 */
	@Override
	public void linkCleanDatabase(TransformationContext context) {
		// TODO Auto-generated method stub		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @param context {@inheritDoc}
	 */
	@Override
	public void linkByConfigFiles(TransformationContext context) {
		File[] files = context.getTransformerDirectory().listFiles();
		LOG.info("{} configuration files found", files.length);
		for (File file:files) {
			if (file.getName().endsWith(".xml")) {
				LOG.info("Calling Silk with configuration file: {}", file.getAbsolutePath());
				Silk.executeFile(file, null, Silk.DefaultThreads(), true);
				LOG.info("Linking finished.");
			}
		}
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
	private List<String> loadRules(String transformerConfiguration, LinkerDao dao ) 
			throws SQLException, QueryException {
		LOG.info("Loading rule groups: {}", transformerConfiguration);
		String[] ruleGroupArray = transformerConfiguration.split(",");

		return dao.loadRules(ruleGroupArray);
	}
}
