package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.DebugGraphFileLoader;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.RulesModel;
import cz.cuni.mff.odcleanstore.shared.UniqueGraphNameGenerator;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * DataNormalizerImpl implements the default Data Normalization for ODCS
 * 
 * It is meant to be used over dirty database only.
 *
 * @author Jakub Daniel
 */
public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {
		try {
			ConfigLoader.loadConfig();
			Map<String, GraphModification> result = new DataNormalizerImpl(0).debugRules(new FileInputStream(System.getProperty("user.home") + "/odcleanstore/debugDN.ttl"),
					prepareContext(
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba"),
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")));
			
			Iterator<String> i = result.keySet().iterator();
			
			while (i.hasNext()) {
				String graph = i.next();

				System.err.println(graph);
				
				Iterator<Rule> j = result.get(graph).getRuleIterator();
				
				while (j.hasNext()) {
					Rule rule = j.next();
					
					Iterator<TripleModification> k;
					
					k = result.get(graph).getModificationsByRule(rule).getInsertions().iterator();
					
					while (k.hasNext()) {
						TripleModification modification = k.next();
						
						System.err.println(modification.s + " " + modification.p + " " + modification.o + " INSERTED (Rule #" + rule.getId() + ")");
						System.err.println();
					}
					
					k = result.get(graph).getModificationsByRule(rule).getDeletions().iterator();
					
					while (k.hasNext()) {
						TripleModification modification = k.next();
						
						System.err.println(modification.s + " " + modification.p + " " + modification.o + " DELETED (Rule #" + rule.getId() + ")");
						System.err.println();
					}
				}
				
				System.err.println();
				System.err.println();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(DataNormalizerImpl.class);
	
	private static final String backupQueryFormat = "SPARQL INSERT INTO <%s> {?s ?p ?o} WHERE {GRAPH <%s> {?s ?p ?o}}";
	private static final String selectQueryFormat = "SPARQL SELECT ?s ?p ?o FROM <%s> WHERE {?s ?p ?o}"; 
	private static final String diffQueryFormat = "SPARQL DELETE FROM <%s> {?s ?p ?o} WHERE {GRAPH <%s> {?s ?p ?o}}";
	private static final String dropBackupQueryFormat = "SPARQL CLEAR GRAPH <%s>";

	/**
	 * The following describer inner state of the transformer
	 *   what database it works on
	 *   what graph does it transform
	 *   
	 * These need to be set consistently at each transformation
	 */
	private TransformedGraph inputGraph;
	private TransformationContext context;

	/**
	 * At construction the transformer is bound to use rules from particular rule groups
	 * 
	 * Any arbitrary number of groups can be used
	 * 
	 * The groups are selected by their IDs or Labels
	 * 
	 * Either one needs to be not null after the transformer construction
	 */
	private Integer[] groupIds = null;
	private String[] groupLabels = null;

	private Collection<Rule> rules;

	/**
	 * constructs new data normalizer
	 * @param groupIds the IDs of the rule groups to be used by the new instance
	 */
	public DataNormalizerImpl (Integer... groupIds) {
		this.groupIds = groupIds;
	}

	/**
	 * constructs new data normalizer
	 * @param groupIds the Labels of the rule groups to be used by the new instance
	 */	
	public DataNormalizerImpl (String... groupLabels) {
		this.groupLabels = groupLabels;
	}

	/**
	 * Connection to dirty database (needed in all cases to work on a new graph or a copy of an existing one)
	 */
	private VirtuosoConnectionWrapper dirtyConnection;

	/**
	 * constructs new connection to the dirty database.
	 * 
	 * @return wrapped connection to the dirty database
	 * @throws ConnectionException
	 */
	private VirtuosoConnectionWrapper getDirtyConnection () throws ConnectionException {
        if (dirtyConnection == null) {
        	dirtyConnection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());
       	}
		return dirtyConnection;
	}

	/**
	 * makes sure the connection to the dirty database is closed and not referenced
	 */
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

	/**
	 * constructs input graph for the transformer interface
	 * @param name the name of the graph to be passed to the transformer
	 * @return the input graph
	 */
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

	/**
	 * constructs context for the transformer interface
	 * @param clean the clean database connection credentials
	 * @param dirty the dirty database connection credentials
	 * @return the context
	 */
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

	/**
	 * collects information about graph transformations
	 * @param source stream containing TriG or RDF/XML input graph(s)
	 * @param context context containing clean and dirty database connection credentials
	 * @return per graph specification of modifications
	 * @throws TransformerException
	 */
	public Map<String, GraphModification> debugRules (InputStream source, TransformationContext context)
			throws TransformerException {
		/**
		 * Prepare fallback empty collection of input graphs (map original names to temporary names)
		 */
		HashMap<String, String> graphs = new HashMap<String, String>();
		
		/**
		 * Prepare graph loader connected to the dirty database 
		 */
		DebugGraphFileLoader loader = new DebugGraphFileLoader(context.getDirtyDatabaseCredentials());
		
		try {
			/**
			 * Load the graphs from the input stream (let default name discriminator be the name of this class) 
			 */
			graphs = loader.load(source, this.getClass().getSimpleName());
			
			Collection<String> originalGraphs = graphs.keySet();
			
			/**
			 * Start collecting modifications of the individual graphs
			 */
			Map<String, GraphModification> result = new HashMap<String, GraphModification>();
			
			Iterator<String> it = originalGraphs.iterator();
			
			while (it.hasNext()) {
				String originalName = it.next();
				String temporaryName = graphs.get(originalName);
				
				GraphModification subResult = getGraphModifications(temporaryName,
						context.getCleanDatabaseCredentials(),
						context.getDirtyDatabaseCredentials());
				
				result.put(originalName, subResult);
			}
			
			return result;
		} catch (Exception e) {
			LOG.error("Debugging of Data Normalization rules failed: " + e.getMessage());
			
			throw new TransformerException(e);
		} finally {
			/**
			 * Always make sure all the graphs are thrown away after all the results are collected or an error
			 * occurs
			 */
			loader.unload(graphs);
		}
	}

	/**
	 * transforms graph in the dirty database
	 * @param inputGraph the graph to be transformed
	 * @param context the context specifying the connection credentials
	 */
	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		this.inputGraph = inputGraph;
		this.context = context;
		
		try
		{
			loadRules();
			applyRules();
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		LOG.info(String.format("Data Normalization applied to graph %s", inputGraph.getGraphName()));
	}
	
	/**
	 * Unsupported action
	 * 
	 * The policy of ODCS does not allow cleaned graphs to be normalized again as normalization cannot be
	 * reverted and multiple applications of the same rules may cause further changes (absence of idempotence)
	 */
	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		throw new TransformerException("Data normalization is supposed to be applied to new graphs.");
	}

	/**
	 * Triple that has either been deleted or inserted to a graph in one step of the normalization process
	 * @author Jakub Daniel
	 */
	public class TripleModification {
		String s;
		String p;
		String o;
		
		public TripleModification(String s, String p, String o) {			
			this.s = s;
			this.p = p;
			this.o = o;
		}
	}

	/**
	 * Collection of all the insertions and deletions that were applied by a certain rule
	 * @author Jakub Daniel
	 */
	public class RuleModification {
		private Collection<TripleModification> insertions = new HashSet<TripleModification>();
		private Collection<TripleModification> deletions = new HashSet<TripleModification>();
		
		public void addInsertion(String s, String p, String o) {
			insertions.add(new TripleModification(s, p, o));
		}
		
		public void addDeletion(String s, String p, String o) {
			deletions.add(new TripleModification(s, p, o));
		}
		
		public Collection<TripleModification> getInsertions() {
			return insertions;
		}
		
		public Collection<TripleModification> getDeletions() {
			return deletions;
		}
	}

	/**
	 * The collection of all modifications done to a graph (grouped by the rules that did them)
	 * @author Jakub Daniel
	 */
	public class GraphModification {
		private Map<Rule, RuleModification> modifications = new HashMap<Rule, RuleModification>();
		
		public void addInsertion (Rule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				/**
				 * Extend an existing modification done by a certain rule
				 */
				modifications.get(rule).addInsertion(s, p, o);
			} else {
				/**
				 * Add new modification that corresponds to a certain rule
				 */
				RuleModification subModifications = new RuleModification();
				
				subModifications.addInsertion(s, p, o);
				
				modifications.put(rule, subModifications);
			}
		}
		
		public void addDeletion(Rule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				/**
				 * Extend an existing modification done by a certain rule
				 */
				modifications.get(rule).addDeletion(s, p, o);
			} else {
				/**
				 * Add new modification that corresponds to a certain rule
				 */
				RuleModification subModifications = new RuleModification();
				
				subModifications.addDeletion(s, p, o);
				
				modifications.put(rule, subModifications);
			}
		}
		
		public Iterator<Rule> getRuleIterator() {
			return modifications.keySet().iterator();
		}
		
		public RuleModification getModificationsByRule(Rule rule) {
			return modifications.get(rule);
		}
	}

	/**
	 * collects modifications that are done to the given graph 
	 * @param graphName name of the graph to be transformed
	 * @param clean the clean database connection credentials
	 * @param source the source database (the one where the transformed graph is - normally dirty database) connection credentials
	 * @return the collection of graph modifications grouped by individual rules
	 * @throws TransformerException
	 */
	public GraphModification getGraphModifications(final String graphName,
			final JDBCConnectionCredentials clean,
			final JDBCConnectionCredentials source)
			throws TransformerException {
		this.inputGraph = prepareInputGraph(graphName);
		this.context = prepareContext(clean, source);
		
		GraphModification modifications = new GraphModification();
		
		try
		{
			loadRules();
			
			/**
			 * Unlike during the transformation of graph running through the pipeline
			 * collect the information about the whole process
			 */
			applyRules(modifications);
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		return modifications;
	}

	/**
	 * selects rules to be used according to the specified rule groups
	 * @throws DataNormalizationException
	 */
	private void loadRules() throws DataNormalizationException {
		RulesModel model = new RulesModel(context.getCleanDatabaseCredentials());

		/**
		 * Either IDs or Labels need to be specified
		 */
		if (groupIds != null) {
			rules = model.getRules(groupIds);
		} else {
			rules = model.getRules(groupLabels);
		}
	}

	/**
	 * applies all selected rules to the current input graph
	 * @throws DataNormalizationException
	 */
	private void applyRules () throws DataNormalizationException {
		applyRules(null);
	}

	/**
	 * applies all selected rules to the current input graph
	 * @param collection to be filled with the modifications in case it is not null
	 * @throws DataNormalizationException
	 */
	private void applyRules(GraphModification modifications) throws DataNormalizationException {
		try {
			getDirtyConnection();
			
			Iterator<Rule> i = rules.iterator();
			
			/**
			 * Ensure that the graph is either transformed completely or not at all
			 */
			getDirtyConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
			
			while (i.hasNext()) {
				Rule rule = i.next();

				performRule(rule, modifications);
			}
			
			getDirtyConnection().commit();
		} catch (ConnectionException e) {
			throw new DataNormalizationException(e);
		} catch (QueryException e) {
			throw new DataNormalizationException(e);
		} catch (SQLException e) {
			throw new DataNormalizationException(e);
		}
	}

	
	/**
	 * transforms the graph by one rule
	 * @param rule the rule to be applied to the currently transformed graph
	 * @param modifications collection to be filled with graph modifications done by this rule (in case it is not null)
	 * @throws DataNormalizationException
	 * @throws ConnectionException
	 * @throws QueryException
	 * @throws SQLException
	 */
	private void performRule(Rule rule, GraphModification modifications) throws DataNormalizationException, ConnectionException, QueryException, SQLException {
		String[] components = rule.getComponents(inputGraph.getGraphName());

		if (modifications == null) {
			/**
			 * In case there is no interest in what was changed just perform all the components in the correct order
			 */
			for (int j = 0; j < components.length; ++j) {
				getDirtyConnection().execute(components[j]);
			}
		} else {
			/**
			 * In case we need to know what changed we need to create copies of the graph to compare them after
			 * the rule was applied
			 */
			UniqueGraphNameGenerator generator = new UniqueGraphNameGenerator("http://example.com/" + this.getClass().getSimpleName() + "/diff/", context.getDirtyDatabaseCredentials());
			String original = "";
			String modified = "";
		
			try {
				original = generator.nextURI(0);
				getDirtyConnection().execute(String.format(backupQueryFormat, original, inputGraph.getGraphName()));

				for (int j = 0; j < components.length; ++j) {
					getDirtyConnection().execute(components[j]);
				}

				modified = generator.nextURI(1);
				getDirtyConnection().execute(String.format(backupQueryFormat, modified, inputGraph.getGraphName()));

				/**
				 * Unfortunatelly "SELECT ?s ?p ?o WHERE {{GRAPH <%s> {?s ?p ?o}} MINUS {GRAPH <%s> {?s ?p ?o}}}"
				 * throws "Internal error: 'output:valmode' declaration conflicts with 'output:format'"
				 * 
				 * Therefore it is necessary to first create graphs with differences.
				 */
				getDirtyConnection().execute(String.format(diffQueryFormat, modified, original));
				getDirtyConnection().execute(String.format(diffQueryFormat, original, inputGraph.getGraphName()));

				WrappedResultSet inserted = getDirtyConnection().executeSelect(String.format(selectQueryFormat, modified));
				
				/**
				 * All that is new to the transformed graph are insertions done by this rule (one of its components)
				 */
				while (inserted.next()) {
					modifications.addInsertion(rule,
							inserted.getString("s"),
							inserted.getString("p"),
							inserted.getString("o"));
				}

				WrappedResultSet deleted = getDirtyConnection().executeSelect(String.format(selectQueryFormat, original));				

				/**
				 * All that is missing from the transformed graph are deletions done by this rule (one of its components)
				 */
				while (deleted.next()) {
					modifications.addDeletion(rule,
							deleted.getString("s"),
							deleted.getString("p"),
							deleted.getString("o"));
				}
			} finally {
				try {
					getDirtyConnection().execute(String.format(dropBackupQueryFormat, original));
				} finally {}
				try {
					getDirtyConnection().execute(String.format(dropBackupQueryFormat, modified));
				} finally {}
			}
		}
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
