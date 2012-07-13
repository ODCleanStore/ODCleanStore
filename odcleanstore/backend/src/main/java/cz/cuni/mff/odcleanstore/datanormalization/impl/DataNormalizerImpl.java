package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.shared.UniqueGraphNameGenerator;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {
		try {
			Map<String, GraphModification> result = new DataNormalizerImpl().debugRules(new FileInputStream(System.getProperty("user.home") + "/odcleanstore/debugDN.rdf"),
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
	
	public Map<String, GraphModification> debugRules (InputStream source, TransformationContext context)
			throws TransformerException {
		HashMap<String, String> graphs = new HashMap<String, String>();
		DebugGraphFileLoader loader = new DebugGraphFileLoader(context.getDirtyDatabaseCredentials());
		
		try {
			graphs = loader.load(source, this.getClass().getSimpleName());
			
			Collection<String> originalGraphs = graphs.keySet();
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
		
		public TripleModification(String s, String p, String o) {			
			this.s = s;
			this.p = p;
			this.o = o;
		}
	}
	
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
	
	public class GraphModification {
		private Map<Rule, RuleModification> modifications = new HashMap<Rule, RuleModification>();
		
		public void addInsertion (Rule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				modifications.get(rule).addInsertion(s, p, o);
			} else {
				RuleModification subModifications = new RuleModification();
				
				subModifications.addInsertion(s, p, o);
				
				modifications.put(rule, subModifications);
			}
		}
		
		public void addDeletion(Rule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				modifications.get(rule).addDeletion(s, p, o);
			} else {
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
			applyRules(modifications);
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		return modifications;
	}
	
	private void loadRules() throws DataNormalizationException {
		rules = new ArrayList<Rule>();
		
		/**
		 * DEBUG rules
		 */
		rules.add(new Rule(0,
				EnumRuleComponentType.RULE_COMPONENT_INSERT,
					"{?a ?b ?y} WHERE {GRAPH $$graph$$ {SELECT ?a ?b fn:replace(str(?c), \".\", \"*\") AS ?y WHERE {?a ?b ?c}}}",
				EnumRuleComponentType.RULE_COMPONENT_DELETE,
					"{?a ?b ?c} WHERE {GRAPH $$graph$$ {?a ?b ?c} FILTER (contains(str(?c), \"*\") = false)}"
				));
		rules.add(new Rule(1,
				EnumRuleComponentType.RULE_COMPONENT_INSERT,
					"{?a <http://example.com/#test> ?b} WHERE {GRAPH $$graph$$ {?a ?b ?c} FILTER (contains(str(?c), \"*******\"))}"));
	}

	private void applyRules(GraphModification modifications) throws DataNormalizationException {
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
	
	private void performRule(Rule rule, GraphModification modifications) throws DataNormalizationException, ConnectionException, QueryException, SQLException {
		String[] components = rule.getComponents(inputGraph.getGraphName());

		if (modifications == null) {
			for (int j = 0; j < components.length; ++j) {
				getDirtyConnection().execute(components[j]);
			}
		} else {
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
				
				while (inserted.next()) {
					modifications.addInsertion(rule,
							inserted.getString("s"),
							inserted.getString("p"),
							inserted.getString("o"));
				}

				WrappedResultSet deleted = getDirtyConnection().executeSelect(String.format(selectQueryFormat, original));				
		
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
