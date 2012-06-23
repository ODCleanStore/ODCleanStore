package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.qualityassessment.*;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.RulesModel;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
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
			applyRules();

			storeResults();
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		} finally {
			closeDirtyConnection();
		}

		LOG.info(String.format("Quality Assessment done for graph %s, %d rules tested, %d violations, score %f",
				inputGraph.getGraphName(), rules.size(), violations, score));
	}

	/**
	 * Analyse what rules should be applied (find out what rule group is demanded)
	 */
	protected void loadRules() throws QualityAssessmentException {
		RulesModel model = new RulesModel(context.getCleanDatabaseCredentials());

		int group = 0;
		
		rules = model.getRules(group);
	}

	/**
	 * Find out what rules are violated and change the score and trace accordingly.
	 */
	protected void applyRules() throws QualityAssessmentException {

		Iterator<Rule> iterator = rules.iterator();

		while (iterator.hasNext()) {
			Rule rule = iterator.next();

			applyRule(rule);
		}
	}

	/**
	 * Applies all the selected rules on the input graph
	 */
	protected void applyRule(Rule rule) throws QualityAssessmentException {
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
