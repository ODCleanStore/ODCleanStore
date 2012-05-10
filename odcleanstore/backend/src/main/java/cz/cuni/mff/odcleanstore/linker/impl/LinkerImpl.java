package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
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
	
	/** 
	 * URI of graph to store generated links to 
	 */
	private static final String LINKS_GRAPH_NAME = "http://odcs.cz/generatedLinks";
	
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
		String config = context.getTransformerConfiguration();
		if (config == null || config.isEmpty()) {
			linkByConfigFiles(context);
		} else {
			LinkerDao dao;
			File configFile = null;
			try {
				// TODO get SparqlEndpoint AND JDBC connection
				dao = LinkerDao.getInstance(context.getCleanDatabaseEndpoint());
				List<String> rawRules = loadRules(context.getTransformerConfiguration(), dao);
				List<RDFprefix> prefixes = RDFPrefixesLoader.loadPrefixes(context.getCleanDatabaseEndpoint());
			
				configFile = ConfigBuilder.createLinkConfigFile(rawRules, prefixes, inputGraph, context);
				
				inputGraph.addAttachedGraph(LINKS_GRAPH_NAME);
			
				Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
				
				configFile.delete();
			} catch (SQLException e) {
				throw new TransformerException(e);
			} catch (ConnectionException e) {
				throw new TransformerException(e);
			} catch (QueryException e) {
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
		for (File file:files) {
			if (file.getName().endsWith(".xml")) {
				Silk.executeFile(file, null, Silk.DefaultThreads(), true);
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
		
		String[] ruleGroupArray = transformerConfiguration.split(",");

		return dao.loadRules(ruleGroupArray);
	}
}
