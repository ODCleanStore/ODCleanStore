package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAggregator;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

public class QualityAggregatorImpl implements QualityAggregator {
	private static final Logger LOG = LoggerFactory.getLogger(QualityAggregatorImpl.class);	
	
	public static void main(String[] args) {
		try {
			new QualityAggregatorImpl().transformExistingGraph(new TransformedGraph () {

				@Override
				public String getGraphName() {
					// TODO Auto-generated method stub
					return "http://opendata.cz/data/namedGraph/1";
				}

				@Override
				public String getGraphId() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getMetadataGraphName() {
					// TODO Auto-generated method stub
					return "http://opendata.cz/data/metadata";
				}

				@Override
				public Collection<String> getAttachedGraphNames() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void addAttachedGraph(String attachedGraphName)
						throws TransformedGraphException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void deleteGraph() throws TransformedGraphException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isDeleted() {
					// TODO Auto-generated method stub
					return false;
				}
				
			}, new TransformationContext() {

				@Override
				public ConnectionCredentials getDirtyDatabaseCredentials() {
					// TODO Auto-generated method stub
					return new ConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba");
				}

				@Override
				public ConnectionCredentials getCleanDatabaseCredentials() {
					// TODO Auto-generated method stub
					return new ConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
				}

				@Override
				public String getTransformerConfiguration() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public File getTransformerDirectory() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public EnumTransformationType getTransformationType() {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		try
		{
			endpoint = context.getCleanDatabaseCredentials();
		
			final String graph = inputGraph.getGraphName();
			final String metadataGraph = inputGraph.getMetadataGraphName();
			final String publisher = getGraphPublisher(graph, metadataGraph);
			
			if (publisher != null) {
				try {
					final String dropOutdated = "SPARQL DELETE {<" + publisher + "> <" + ODCS.publisherScore + "> ?score}";
					
					System.err.println(dropOutdated);
					getConnection().execute(dropOutdated);
					
					final String computeUpdated = "SPARQL SELECT AVG(?score) WHERE {?graph <" + ODCS.score + "> ?score; <" + W3P.publishedBy + "> <" + publisher + ">}";
					
					System.err.println(computeUpdated);
					WrappedResultSet rs = getConnection().executeSelect(computeUpdated);

					Double score;

					if (rs.next()) {
						score = rs.getDouble(1);
					
						LOG.info("Publisher <" + publisher + "> scored " + score + ".");
					} else {
						throw new QualityAssessmentException("Publisher has no score.");
					}

					final String storeUpdated = "SPARQL INSERT DATA INTO <" + metadataGraph + "> {<" + publisher + "> <" + ODCS.publisherScore + "> \"" + score + "\"^^xsd:double}";
				
					getConnection().execute(storeUpdated);
				} catch (ConnectionException e) {
					throw new TransformerException(e);
				} catch (QueryException e) {
					throw new TransformerException(e);
				} catch (SQLException e) {
					throw new TransformerException(e);
				}
			}
			
			try {
				closeConnection();
			} catch (ConnectionException e) {
			}
		} catch (QualityAssessmentException e) {
			throw new TransformerException(e);
		}
	}

	@Override
	public void shutdown() throws TransformerException {
	}
	
	private ConnectionCredentials endpoint;
	private VirtuosoConnectionWrapper connection;

	private VirtuosoConnectionWrapper getConnection () throws ConnectionException {
        if (connection == null) {
        	connection = VirtuosoConnectionWrapper.createConnection(endpoint);
       	}
		return connection;
	}

	private void closeConnection() throws ConnectionException {
        if (connection != null) {
        	connection.close();
        	connection = null;
        }
	}
	
	private String getGraphPublisher (final String graph, final String metadataGraph) throws QualityAssessmentException {	
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
			throw new QualityAssessmentException(e.getMessage());
		} catch (SQLException e) {
			throw new QualityAssessmentException(e.getMessage());
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
		}
		
		return publisher;
	}
}
