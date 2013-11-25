package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAggregator;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.openrdf.model.vocabulary.XMLSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Locale;

/**
 * Default implementation of the Quality Aggregator
 *
 * Compute average score of all graphs known to us (stored in db, including this one)
 * and update its score in database in ODCSInternal.aggregatedPublisherScoreGraphUri
 * graph
 *
 * @author Jakub Daniel
 *
 */
public class QualityAggregatorImpl implements QualityAggregator {
	private static final Logger LOG = LoggerFactory.getLogger(QualityAggregatorImpl.class);

	private final static String dropOutdatedQueryFormat = "SPARQL DELETE FROM <%s> {?publisher <" + ODCS.PUBLISHER_SCORE + "> ?score} WHERE {?publisher <" + ODCS.PUBLISHER_SCORE + "> ?score. FILTER (?publisher = <%s>)}";
	private final static String computeSumUpdatedQueryFormat = "SPARQL SELECT SUM(?score) WHERE {?graph <" + ODCS.SCORE + "> ?score; <" + ODCS.PUBLISHED_BY + "> <%s>}";
	private final static String computeCountUpdatedQueryFormat = "SPARQL SELECT COUNT(?score) WHERE {?graph <" + ODCS.SCORE + "> ?score; <" + ODCS.PUBLISHED_BY + "> <%s>}";
	private final static String storeUpdatedQueryFormat = "SPARQL INSERT DATA INTO <%s> {<%s> <" + ODCS.PUBLISHER_SCORE + "> \"%f\"^^<" + XMLSchema.DOUBLE + ">}";
	private final static String graphPublisherQueryFormat = "SPARQL SELECT ?publisher FROM <%s> WHERE {<%s> <" + ODCS.PUBLISHED_BY + "> ?publisher}";
	private final static String graphScoreQueryFormat = "SPARQL SELECT ?score FROM <%s> WHERE {<%s> <" + ODCS.SCORE + "> ?score}";

	private TransformationContext context;

	/**
	 * Compute aggregated score of all scores of graph in the clean database and the score of the new graph
	 * (restrained to graphs published by the same publisher who published the currently processed graph).
	 */
	@Override
	public void transformGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		this.context = context;

		try {
            switch (context.getTransformationType()) {
            case EXISTING:
                Double outdatedScore = getGraphScore(inputGraph.getGraphName(), inputGraph.getMetadataGraphName(),
                        getCleanConnection());
                Double updatedScore = getGraphScore(inputGraph.getGraphName(), inputGraph.getMetadataGraphName(),
                        getDirtyConnection());

                updatePublisherScore(inputGraph.getGraphName(),
                        inputGraph.getMetadataGraphName(),
                        updatedScore - outdatedScore,
                        0);
                break;
            case NEW:
                Double newScore = getGraphScore(inputGraph.getGraphName(), inputGraph.getMetadataGraphName(),
                        getDirtyConnection());

                updatePublisherScore(inputGraph.getGraphName(),
                        inputGraph.getMetadataGraphName(),
                        newScore,
                        1);
                break;
            default:
                // we shouldn't get here
                break;
            }
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		} catch (DatabaseException e) {
			throw new TransformerException(e);
		} finally {
			closeCleanConnection();
			closeDirtyConnection();
		}
	}

	/**
	 * Takes all the graphs from clean database that share the publisher with the input graph. Adds delta (score of deltaCount other graphs) into the average.
	 */
	private void updatePublisherScore (String graph,
			String metadataGraph,
			Double delta,
			Integer deltaCount) throws TransformerException {
		try
		{
			final String publisher = getGraphPublisher(graph, metadataGraph);

			if (publisher != null) {
				try {
					final String computeSumUpdated = String.format(Locale.ROOT, computeSumUpdatedQueryFormat, publisher);
					final String computeCountUpdated = String.format(Locale.ROOT, computeCountUpdatedQueryFormat, publisher);

					WrappedResultSet rsSum = getCleanConnection().executeSelect(computeSumUpdated);
					WrappedResultSet rsCount = getCleanConnection().executeSelect(computeCountUpdated);

					Double score;

					if (rsSum.next() && rsCount.next()) {
						//Clean DB: the graph is also present in clean DB (only its score may change for the copy in dirty DB) => no division by zero
						//Dirty DB: deltaCount == 1 => no division by zero

					    Double sum = rsSum.getDouble(1) != null ? rsSum.getDouble(1) : 0F;
					    Double count = rsCount.getDouble(1) != null ? rsCount.getDouble(1) : 0F;
						if (sum == null || count == null || count + deltaCount == 0)
						{
							//Very special cases => abort assignment of score for the current publisher
							return;
						}

						score = (sum + delta) / (count + deltaCount);

						LOG.info("Publisher <" + publisher + "> scored " + score + ".");
					} else {
						throw new QualityAssessmentException("Publisher has no score.");
					}

					getCleanConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL);

					final String dropOutdated = String.format(Locale.ROOT, dropOutdatedQueryFormat, ODCSInternal.AGGREGATED_PUBLISHER_SCORE_GRAPH_URI, publisher);
					getCleanConnection().execute(dropOutdated);

					final String storeUpdated = String.format(Locale.ROOT, storeUpdatedQueryFormat, ODCSInternal.AGGREGATED_PUBLISHER_SCORE_GRAPH_URI, publisher, score);
					getCleanConnection().execute(storeUpdated);

					getCleanConnection().commit();
				} catch (DatabaseException e) {
					throw new TransformerException(e);
				} catch (SQLException e) {
					throw new TransformerException(e);
				}
			}
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		}
	}

	@Override
	public void shutdown() throws TransformerException {
	}

	private VirtuosoConnectionWrapper cleanConnection;
	private VirtuosoConnectionWrapper dirtyConnection;

	private VirtuosoConnectionWrapper getCleanConnection () throws DatabaseException {
        if (cleanConnection == null) {
        	cleanConnection = VirtuosoConnectionFactory.createJDBCConnection(context.getCleanDatabaseCredentials());
       	}
		return cleanConnection;
	}

	private void closeCleanConnection() {
		try {
			if (cleanConnection != null) {
				cleanConnection.close();
			}
		} catch (DatabaseException e) {
		} finally {
			cleanConnection = null;
		}
	}

	private VirtuosoConnectionWrapper getDirtyConnection () throws DatabaseException {
        if (dirtyConnection == null) {
        	dirtyConnection = VirtuosoConnectionFactory.createJDBCConnection(context.getDirtyDatabaseCredentials());
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

	/**
	 * @param graph
	 * @param metadataGraph
	 * @return The publisher of the given graph.
	 * @throws QualityAssessmentException
	 */
	private String getGraphPublisher (final String graph, final String metadataGraph) throws QualityAssessmentException {
		final String query = String.format(Locale.ROOT, graphPublisherQueryFormat, metadataGraph, graph);
		WrappedResultSet results = null;
		String publisher = null;

		try
		{
			results = getDirtyConnection().executeSelect(query);

			if (results.next()) {
				publisher = results.getString("publisher");
			}
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
		}

		return publisher;
	}

	/**
	 * @param graph
	 * @param metadataGraph
	 * @return Score of the given graph.
	 * @throws QualityAssessmentException
	 */
	private Double getGraphScore (final String graph,
			final String metadataGraph,
			final VirtuosoConnectionWrapper connection) throws QualityAssessmentException {
		final String query = String.format(Locale.ROOT, graphScoreQueryFormat, metadataGraph, graph);
		WrappedResultSet results = null;
		Double score = 0.0;

		try
		{
			results = connection.executeSelect(query);

			if (results.next()) {
				score = results.getDouble("score");
			}
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
		}

		return score;
	}
}
