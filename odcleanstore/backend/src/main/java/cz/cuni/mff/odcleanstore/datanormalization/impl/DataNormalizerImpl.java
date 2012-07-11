package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.DebugGraphFileLoader;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {
		try {
			new DataNormalizerImpl().debugRules(new FileInputStream(System.getProperty("user.home") + "/odcleanstore/debugDN.ttl"),
					prepareContext(
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba"),
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(DataNormalizerImpl.class);
	
	private TransformedGraph inputGraph;
	private TransformationContext context;
	
	private Collection<Rule> rules;

	/**
	 * Connection to dirty database (needed in all cases to work on a new graph or a copy of an existing one)
	 */
	private VirtuosoConnectionWrapper dirtyConnection;

	private VirtuosoConnectionWrapper getDirtyConnection () throws ConnectionException {
        if (dirtyConnection == null) {
        	dirtyConnection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());
       	}
		return dirtyConnection;
	}

	private void closeDirtyConnection() {
		try {
			if (dirtyConnection != null) {
				dirtyConnection.close();
			}
		} catch (ConnectionException e) {
		} finally {
			dirtyConnection = null;
		}
	}
	
	private static TransformedGraph prepareInputGraph (final String name) {
		return new TransformedGraph() {

			@Override
			public String getGraphName() {
				return name;
			}
			@Override
			public String getGraphId() {
				return null;
			}
			@Override
			public String getMetadataGraphName() {
				return null;
			}
			@Override
			public Collection<String> getAttachedGraphNames() {
				return null;
			}
			@Override
			public void addAttachedGraph(String attachedGraphName)
					throws TransformedGraphException {				
			}
			@Override
			public void deleteGraph() throws TransformedGraphException {				
			}
			@Override
			public boolean isDeleted() {
				return false;
			}
		};
	}
	
	private static TransformationContext prepareContext (final JDBCConnectionCredentials clean, final JDBCConnectionCredentials dirty) {
		return new TransformationContext() {
			@Override
			public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
				return dirty;
			}
			@Override
			public JDBCConnectionCredentials getCleanDatabaseCredentials() {
				return clean;
			}
			@Override
			public String getTransformerConfiguration() {
				return null;
			}
			@Override
			public File getTransformerDirectory() {
				return null;
			}
			@Override
			public EnumTransformationType getTransformationType() {
				return null;
			}
		};
	}
	
	public InputStream debugRules (InputStream source, TransformationContext context)
			throws TransformerException {
		HashMap<String, String> graphs = new HashMap<String, String>();
		DebugGraphFileLoader loader = new DebugGraphFileLoader(context.getDirtyDatabaseCredentials());
		
		try {
			graphs = loader.load(source, this.getClass().getSimpleName());
			
			Collection<String> temporaryGraphs = graphs.values();
			
			Iterator<String> it = temporaryGraphs.iterator();
			
			while (it.hasNext()) {
				String temporaryName = it.next();
				
				transformNewGraph(prepareInputGraph(temporaryName), context);
				
				/**
				 * FIND INSERTIONS AND DELETIONS PER RULE APPLICATION
				 */
			}
			
			return null;
		} catch (Exception e) {
			LOG.error("Debugging of Data Normalization rules failed: " + e.getMessage());
			
			throw new TransformerException(e);
		} finally {
			loader.unload(graphs);
		}
	}

	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		this.inputGraph = inputGraph;
		this.context = context;
		
		try
		{
			loadRules();
			applyRules(null);
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		LOG.info(String.format("Data Normalization applied to graph %s", inputGraph.getGraphName()));
	}
	
	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		throw new TransformerException("Data normalization is supposed to be applied to new graphs.");
	}
	
	public class TripleModification {
		String s;
		String p;
		String o;

		EnumRuleComponentType type;
		
		Integer rule;
	}
	
	public Map<Integer, Set<TripleModification>> getModifications (final String graphName,
			final JDBCConnectionCredentials clean,
			final JDBCConnectionCredentials source)
			throws TransformerException {
		this.inputGraph = prepareInputGraph(graphName);
		this.context = prepareContext(clean, source);
		
		Map<Integer, Set<TripleModification>> modifications = new HashMap<Integer, Set<TripleModification>>();
		
		try
		{
			loadRules();
			applyRules(modifications);
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		return modifications;
	}
	
	private void loadRules () throws DataNormalizationException {
		rules = new ArrayList<Rule>();
		
		/**
		 * DEBUG rules
		 */
		rules.add(new Rule(
				EnumRuleComponentType.RULE_COMPONENT_INSERT,
					"{?a ?b ?y} WHERE {GRAPH $$graph$$ {SELECT ?a ?b fn:replace(str(?c), \".\", \"*\") AS ?y WHERE {?a ?b ?c}}}"
				));
	}

	private void applyRules (Map<Integer, Set<TripleModification>> modifications) throws DataNormalizationException {
		try {
			getDirtyConnection();
			
			Iterator<Rule> i = rules.iterator();
			
			while (i.hasNext()) {
				Rule rule = i.next();

				getDirtyConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
				
				performRule(rule, modifications);
				
				getDirtyConnection().commit();
			}
		} catch (ConnectionException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (QueryException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (SQLException e) {
			throw new DataNormalizationException(e.getMessage());
		}
	}
	
	private void performRule (Rule rule, Map<Integer, Set<TripleModification>> modifications) throws DataNormalizationException, ConnectionException, QueryException, SQLException {
		String[] components = rule.getComponents(inputGraph.getGraphName());

		for (int j = 0; j < components.length; ++j) {
			//System.err.println(components[j]);

			getDirtyConnection().execute(components[j]);
		}
		
		//UPDATE MODIFICATIONS
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
