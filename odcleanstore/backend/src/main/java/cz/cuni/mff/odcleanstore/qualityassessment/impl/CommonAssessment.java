package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.RulesModel;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A common base for assessment processes.
 *
 * The assessment itself is independent of the database where the graph resides.
 * There is a difference in computing aggregate score for publishers (TODO).
 */
abstract class CommonAssessment {
	protected static final Logger LOG = LoggerFactory.getLogger(CommonAssessment.class);

	protected TransformedGraph inputGraph;
	protected TransformationContext context;

	protected Collection<Rule> rules;

	protected Float score = 1.0f;
	protected List<String> trace = new ArrayList<String>();
	protected Integer violations = 0;

	protected VirtuosoConnectionWrapper connection;

	private VirtuosoConnectionWrapper getConnection () throws ConnectionException {
        if (connection == null) {
        	connection = VirtuosoConnectionWrapper.createConnection(getEndpoint());
       	}
		return connection;
	}

	private void closeConnection() throws ConnectionException {
        if (connection != null) {
			connection.close();
			connection = null;
        }
	}

	/**
	 * To assess the quality it is necessary to load all relevant rules, apply them
	 * and then store the results.
	 */
	protected void assessQuality(TransformedGraph inputGraph,
			TransformationContext context) throws QualityAssessmentException {

		this.inputGraph = inputGraph;
		this.context = context;

		try
		{
			loadRules();
			applyRules();

			storeResults();
		} catch (QualityAssessmentException e) {
			throw e;
		} finally {
			try {
				closeConnection();
			} catch (ConnectionException e) {
				// do nothing
			}
		}

		LOG.info(String.format("Quality Assessment done for graph %s, %d rules tested, %d violations, score %f", inputGraph.getGraphName(), rules.size(), violations, score));
	}

	/**
	 * Let the concrete implementation decide on what endpoint to choose (Clean/Dirty)
	 */
	abstract protected JDBCConnectionCredentials getEndpoint();

	/**
	 * Extract a URI of the graphs publisher if possible
	 *
	 * @return URI of the publisher of the input graph
	 */
	protected String getGraphPublisher () throws QualityAssessmentException {
		final String graph = inputGraph.getGraphName();
		final String metadataGraph = inputGraph.getMetadataGraphName();

		final String query = "SPARQL SELECT ?publisher FROM <" + metadataGraph + "> WHERE {<" + graph + "> <" + W3P.publishedBy + "> ?publisher}";
		WrappedResultSet results = null;
		String publisher = null;

		try
		{
			results = getConnection().executeSelect(query);

			if (results.next()) {
				publisher = results.getString("publisher");
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

		return publisher;
	}

	/**
	 * Analyze the graph (presence of publishedBy property). Choose
	 * all rules that can surely be applied.
	 */
	protected void loadRules() throws QualityAssessmentException {
		RulesModel model = new RulesModel(context.getCleanDatabaseCredentials());

		String publisher = getGraphPublisher();

		if (publisher == null) {
			rules = model.getUnrestrictedRules();
		} else {
			rules = model.getRulesForPublisher(publisher);
		}
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
	 *
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
			results = getConnection().executeSelect(query);

			if (results.next()) {
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

		final String dropOldScore = "SPARQL DELETE FROM <" + metadataGraph + "> {<" + graph + "> <" + ODCS.score + "> ?s} WHERE {<" + graph + "> <" + ODCS.score + "> ?s}";
		final String dropOldScoreTrace = "SPARQL DELETE FROM <" + metadataGraph + "> {<" + graph + "> <" + ODCS.scoreTrace + "> ?s} WHERE {<" + graph + "> <" + ODCS.scoreTrace + "> ?s}";
		final String storeNewScore = "SPARQL INSERT DATA INTO <" + metadataGraph + "> {<" + graph + "> <" + ODCS.score + "> \"" + score + "\"^^xsd:double}";

		/*
		System.err.println(dropOldScore);
		System.err.println(dropOldScoreTrace);
		System.err.println(storeNewScore);
		System.err.println(storeNewScoreTrace);
		*/

		/**
		 * First delete old values for this particular graph in the metadata graph.
		 * Then store the newly obtained values.
		 */
		try {
			getConnection().execute(dropOldScore);
			getConnection().execute(dropOldScoreTrace);
			getConnection().execute(storeNewScore);

			Iterator<String> iterator = trace.iterator();

			while (iterator.hasNext()) {
				String escapedTrace = iterator.next();

				escapedTrace = escapedTrace.replaceAll("'", "\\\\'");

				final String storeNewScoreTrace = "SPARQL INSERT DATA INTO <" + metadataGraph + "> {<" + graph + "> <" + ODCS.scoreTrace + "> '" + escapedTrace + "'^^xsd:string}";

				getConnection().execute(storeNewScoreTrace);
			}
		} catch (DatabaseException e) {
			//LOG.fatal(e.getMessage());
			throw new QualityAssessmentException(e.getMessage());
		}
	}
}
