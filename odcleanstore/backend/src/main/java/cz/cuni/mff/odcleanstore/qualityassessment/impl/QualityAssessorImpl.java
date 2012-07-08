package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.DebugGraphFileLoader;
import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.*;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.RulesModel;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

/**
 * The default quality assessor.
 *
 * Depending on the situation selects implementation of quality assessment
 * and delegates the work to that implementation.
 */
public class QualityAssessorImpl implements QualityAssessor {
	
	public static void main(String[] args) {
		try {
			new QualityAssessorImpl().debugRules(System.getProperty("user.home") + "/odcleanstore/debugQA.ttl",
					"http://opendata.cz/data/namedGraph/1",
					prepareContext(
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba"),
							new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")));
		} catch (TransformerException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * SPARQL queries for Quality Assessor transformation of input graph and metadata graph
	 */
	private final static String dropOldScoreQueryFormat = "SPARQL DELETE FROM <%s> {<%s>" +
			"<" + ODCS.score + "> " +
			"?s} WHERE {<%s> " +
			"<" + ODCS.score + "> ?s}";
	private final static String dropOldScoreTraceQueryFormat = "SPARQL DELETE FROM <%s> {<%s> " +
			"<" + ODCS.scoreTrace + "> " +
			"?s} WHERE {<%s>" +
			"<" + ODCS.scoreTrace + "> ?s}";
	private final static String storeNewScoreQueryFormat =  "SPARQL INSERT DATA INTO <%s> {<%s> " +
			"<" + ODCS.score + "> \"%f\"^^<" + XMLSchema.doubleType + ">}";
	private final static String storeNewScoreTraceQueryFormat = "SPARQL INSERT DATA INTO <%s> {<%s> " +
			"<" + ODCS.scoreTrace + "> " +
			"'%s'^^<" + XMLSchema.stringType + ">}";
	
	private static final Logger LOG = LoggerFactory.getLogger(QualityAssessorImpl.class);
	
	private TransformedGraph inputGraph;
	private TransformationContext context;

	private Collection<Rule> rules;

	private Double score;
	private List<String> trace;
	private Integer violations;

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
	
	private static TransformedGraph prepareInputGraph (final String name, final String metadataName) {
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
				return metadataName;
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
	
	public void debugRules (String sourceFile, String commonMetadataGraph, TransformationContext context)
			throws TransformerException {
		HashMap<String, String> graphs = new HashMap<String, String>();
		DebugGraphFileLoader loader = new DebugGraphFileLoader(context.getDirtyDatabaseCredentials());
		
		try {
			graphs = loader.load(sourceFile, this.getClass().getSimpleName());
			
			if (!graphs.containsKey(commonMetadataGraph)) {
				throw new TransformerException("missing metadata graph");
			}
			
			Collection<String> temporaryGraphs = graphs.values();
			
			Iterator<String> it = temporaryGraphs.iterator();
			
			while (it.hasNext()) {
				String temporaryName = it.next();
				
				/**
				 * Perform QA for all graphs except the metadata graph
				 */
				if (!temporaryName.equals(graphs.get(commonMetadataGraph))) {
					transformNewGraph(prepareInputGraph(temporaryName, graphs.get(commonMetadataGraph)), context);
				}
				
				/**
				 * TODO: COLLECT RESULTS
				 */
			}
		} catch (Exception e) {
			LOG.error("Debugging of Quality Assessment rules failed: " + e.getMessage());
		} finally {
			loader.unload(graphs);
		}
	}
	
	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {

		/**
		 * Both cases involve graphs in dirty database and rules in clean database
		 */
		transformExistingGraph(inputGraph, context);
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		
		/**
		 * The graph is copied into dirty database along with its metadata graph
		 * the updated copies are then used to overwrite the originals in clean
		 * database. This is why both methods (transformExistingGraph,
		 * transformNewGraph) do not differ in Quality Assessment.
		 */
		this.inputGraph = inputGraph;
		this.context = context;

		/**
		 * Start from scratch
		 */
		score = 1.0;
		trace = new ArrayList<String>();
		violations = 0;
		
		try
		{
			loadRules();
			applyRules(null);

			storeResults();
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		LOG.info(String.format("Quality Assessment done for graph %s, %d rules tested, %d violations, score %f",
				inputGraph.getGraphName(), rules.size(), violations, score));
	}
	
	public class GraphScoreWithTrace {
		private Double score;
		private Collection<Rule> trace;
		
		public GraphScoreWithTrace(Double score, Collection<Rule> trace) {
			this.score = score;
			this.trace = trace;
		}
		
		public Double getScore() {
			return score;
		}
		
		public Collection<Rule> getTrace() {
			return trace;
		}
	}
	
	public GraphScoreWithTrace getGraphScoreWithTrace (final String graphName, final JDBCConnectionCredentials credentials)
		throws TransformerException {

		this.inputGraph = prepareInputGraph(graphName, null);
		
		this.context = prepareContext(credentials, credentials);
		
		/**
		 * Start from scratch
		 */
		score = 1.0;
		trace = new ArrayList<String>();
		violations = 0;
		
		Collection<Rule> rules = new ArrayList<Rule>();
		
		try
		{
			loadRules();			
			applyRules(rules);
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}
		
		return new GraphScoreWithTrace(score, rules);
	}

	/**
	 * Analyse what rules should be applied (find out what rule group is demanded)
	 */
	protected void loadRules() throws QualityAssessmentException {
		RulesModel model = new RulesModel(context.getCleanDatabaseCredentials());

		int group = 1;
		
		rules = model.getRules(group);
	}

	/**
	 * Find out what rules are violated and change the score and trace accordingly.
	 */
	protected void applyRules(Collection<Rule> appliedRules) throws QualityAssessmentException {

		Iterator<Rule> iterator = rules.iterator();

		while (iterator.hasNext()) {
			Rule rule = iterator.next();

			applyRule(rule, appliedRules);
		}
	}

	/**
	 * Applies all the selected rules on the input graph
	 */
	protected void applyRule(Rule rule, Collection<Rule> appliedRules) throws QualityAssessmentException {
		String query = rule.toString(inputGraph.getGraphName());

		WrappedResultSet results = null;

		/**
		 * See if the graph matches the rules filter
		 */
		try
		{
			/**
			 * DEBUG: Unfortunately it does not suffice to use SPARQL ASK as long as we
			 * want to use GROUP BY, HAVING
			 */
			results = getDirtyConnection().executeSelect(query);

			if (results.next() && results.getInt(1) > 0) {
				/**
				 * If so, change the graph's score accordingly
				 */
				addCoefficient(rule.getCoefficient());
				logComment(rule.getComment());
				++violations;
				
				if (appliedRules != null) appliedRules.add(rule);
			}
		} catch (DatabaseException e) {
			//LOG.fatal(e.getMessage());
			throw new QualityAssessmentException(e.getMessage());
		} catch (SQLException e) {
			//...
			throw new QualityAssessmentException(e.getMessage());
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
		}
	}

	protected void logComment(String comment) {
		trace.add(comment);
	}

	protected void addCoefficient(Float coefficient) {
		score *= coefficient;
	}

	protected void storeResults() throws QualityAssessmentException {
		final String graph = inputGraph.getGraphName();
		final String metadataGraph = inputGraph.getMetadataGraphName();

		final String dropOldScore = String.format(dropOldScoreQueryFormat,
				metadataGraph,
				graph,
				graph);
		final String dropOldScoreTrace = String.format(dropOldScoreTraceQueryFormat,
				metadataGraph,
				graph,
				graph);
		final String storeNewScore = String.format(storeNewScoreQueryFormat,
				metadataGraph,
				graph,
				score);

		/**
		 * First delete old values for this particular graph in the metadata graph.
		 * Then store the newly obtained values.
		 */
		try {
			getDirtyConnection().execute(dropOldScore);
			getDirtyConnection().execute(dropOldScoreTrace);
			getDirtyConnection().execute(storeNewScore);

			Iterator<String> iterator = trace.iterator();

			while (iterator.hasNext()) {
				String escapedTrace = iterator.next();

				escapedTrace = escapedTrace.replaceAll("'", "\\\\'");

				final String storeNewScoreTrace = String.format(storeNewScoreTraceQueryFormat,
						metadataGraph,
						graph,
						escapedTrace);

				getDirtyConnection().execute(storeNewScoreTrace);
			}
		} catch (DatabaseException e) {
			//LOG.fatal(e.getMessage());
			throw new QualityAssessmentException(e.getMessage());
		}
	}
	
	@Override
    public void shutdown() {
    }
}
