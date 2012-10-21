package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRulesModel;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The default quality assessor.
 *
 * Depending on the situation selects implementation of quality assessment
 * and delegates the work to that implementation.
 */
public class QualityAssessorImpl implements QualityAssessor, Serializable {
	private static final long serialVersionUID = 1L;

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

	private Integer[] groupIds;
	private String[] groupLabels;

	private Collection<QualityAssessmentRule> rules;

	private Double score;
	private List<String> trace;
	private Integer violations;

	public QualityAssessorImpl (Integer... groupIds) {
		this.groupIds = groupIds;
	}

	public QualityAssessorImpl (String... groupLabels) {
		this.groupLabels = groupLabels;
	}

	/**
	 * Connection to dirty database (needed in all cases to work on a new graph or a copy of an existing one)
	 */
	private VirtuosoConnectionWrapper dirtyConnection;

	private VirtuosoConnectionWrapper getDirtyConnection () throws DatabaseException {
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
		} catch (DatabaseException e) {
		} finally {
			dirtyConnection = null;
		}
	}

	private static TransformedGraph prepareInputGraph (
	        final String name, final String metadataName, final String provenanceMetadataName) {
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
			public String getProvenanceMetadataGraphName() {
			    return provenanceMetadataName;
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

	public List<GraphScoreWithTrace> debugRules(
			HashMap<String, String> graphs,
	        TransformationContext context,
	        TableVersion tableVersion)
			throws TransformerException {

		try {
			Collection<String> originalGraphs = graphs.keySet();
			List<GraphScoreWithTrace> result = new ArrayList<GraphScoreWithTrace>();

			Iterator<String> it = originalGraphs.iterator();

			while (it.hasNext()) {
				String originalName = it.next();
				String temporaryName = graphs.get(originalName);

				GraphScoreWithTrace subResult = getGraphScoreWithTrace(temporaryName,
						context.getCleanDatabaseCredentials(),
						context.getDirtyDatabaseCredentials(),
						tableVersion);
				subResult.setGraphName(originalName);

				result.add(subResult);
			}

			return result;
		} catch (Exception e) {
			LOG.error("Debugging of Quality Assessment rules failed: " + e.getMessage());

			throw new TransformerException(e);
		}
	}

	@Override
	public void transformGraph(TransformedGraph inputGraph,
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

		LOG.info(String.format(Locale.ROOT, "Quality Assessment done for graph %s, %d rules tested, %d violations, score %f",
				inputGraph.getGraphName(), rules.size(), violations, score));
	}

	public static class GraphScoreWithTrace implements Serializable {
		private static final long serialVersionUID = 1L;

		private String graphName;
		private Double score;
		private List<QualityAssessmentRule> trace;

		public GraphScoreWithTrace(Double score, List<QualityAssessmentRule> trace) {
			this.score = score;
			this.trace = trace;
		}

		public String getGraphName() {
			return graphName;
		}

		public void setGraphName(String graphName) {
			this.graphName = graphName;
		}

		public Double getScore() {
			return score;
		}

		public List<QualityAssessmentRule> getTrace() {
			return trace;
		}
	}

	//Queries for clean graphs
	public GraphScoreWithTrace getGraphScoreWithTrace (final String graphName,
			final JDBCConnectionCredentials clean)
					throws TransformerException {
		return getGraphScoreWithTrace(graphName, clean, clean, TableVersion.COMMITTED);
	}

	//General version for rule debugging etc.
	public GraphScoreWithTrace getGraphScoreWithTrace (final String graphName,
			final JDBCConnectionCredentials clean,
			final JDBCConnectionCredentials source,
			final TableVersion tableVersion)
		throws TransformerException {

		this.inputGraph = prepareInputGraph(graphName, null, null);

		this.context = prepareContext(clean, source);

		/**
		 * Start from scratch
		 */
		score = 1.0;
		trace = new ArrayList<String>();
		violations = 0;

		List<QualityAssessmentRule> rules = new ArrayList<QualityAssessmentRule>();

		try
		{
			loadRules(tableVersion);
			applyRules(rules);
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		return new GraphScoreWithTrace(score, rules);
	}

	protected void loadRules() throws QualityAssessmentException {
		loadRules(TableVersion.COMMITTED);
	}

	/**
	 * Analyse what rules should be applied (find out what rule group is demanded)
	 */
	protected void loadRules(TableVersion tableVersion) throws QualityAssessmentException {
		QualityAssessmentRulesModel model = new QualityAssessmentRulesModel(context.getCleanDatabaseCredentials(), tableVersion);

		if (groupIds != null) {
			rules = model.getRules(groupIds);
		} else {
			rules = model.getRules(groupLabels);
		}

		LOG.info(String.format(Locale.ROOT, "Quality Assessment selected %d rules.", rules.size()));
	}

	/**
	 * Find out what rules are violated and change the score and trace accordingly.
	 */
	protected void applyRules(Collection<QualityAssessmentRule> appliedRules) throws QualityAssessmentException {

		Iterator<QualityAssessmentRule> iterator = rules.iterator();

		while (iterator.hasNext()) {
			QualityAssessmentRule rule = iterator.next();

			applyRule(rule, appliedRules);
		}
	}

	/**
	 * Applies all the selected rules on the input graph
	 */
	protected void applyRule(QualityAssessmentRule rule, Collection<QualityAssessmentRule> appliedRules) throws QualityAssessmentException {
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
				logComment(rule.getDescription());
				++violations;

				if (appliedRules != null) appliedRules.add(rule);

				LOG.info(String.format("Rule %d matched%s", rule.getId(), rule.getDescription() != null ? "\n\n(" + rule.getDescription() + ")\n\n" : ""));
			} else {
				LOG.info(String.format("Rule %d did not match%s", rule.getId(), rule.getDescription() != null ? "\n\n(" + rule.getDescription() + ")\n\n" : ""));
			}
		} catch (DatabaseException e) {
			LOG.error(String.format(Locale.ROOT, "Failed to apply rule %d: %s\n\n%s\n\n%s", rule.getId(), rule.getDescription() != null ? rule.getDescription() : "", query, e.getMessage()));
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			LOG.error(String.format(Locale.ROOT, "Failed to apply rule %d: %s\n\n%s\n\n%s", rule.getId(), rule.getDescription() != null ? rule.getDescription() : "", query, e.getMessage()));
			throw new QualityAssessmentException(e);
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
		}
	}

	protected void logComment(String comment) {
		trace.add(comment);
	}

	protected void addCoefficient(Double coefficient) {
		score *= coefficient;
	}

	protected void storeResults() throws QualityAssessmentException {
		final String graph = inputGraph.getGraphName();
		final String metadataGraph = inputGraph.getMetadataGraphName();

		final String dropOldScore = String.format(Locale.ROOT, dropOldScoreQueryFormat,
				metadataGraph,
				graph,
				graph);
		final String dropOldScoreTrace = String.format(Locale.ROOT, dropOldScoreTraceQueryFormat,
				metadataGraph,
				graph,
				graph);
		final String storeNewScore = String.format(Locale.ROOT, storeNewScoreQueryFormat,
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

				Pattern charsToBeRemoved = Pattern.compile("[\\x00-\\x09\\x0E-\\x1F]");
				Pattern charsToBeEscaped = Pattern.compile("([\"'`\\\\])");

				escapedTrace = charsToBeRemoved.matcher(escapedTrace).replaceAll("");
				escapedTrace = charsToBeEscaped.matcher(escapedTrace).replaceAll("\\\\$1");

				final String storeNewScoreTrace = String.format(Locale.ROOT, storeNewScoreTraceQueryFormat,
						metadataGraph,
						graph,
						escapedTrace);

				getDirtyConnection().execute(storeNewScoreTrace);
			}
		} catch (DatabaseException e) {
			//LOG.fatal(e.getMessage());
			throw new QualityAssessmentException(e);
		}
	}

	@Override
    public void shutdown() {
    }
}
