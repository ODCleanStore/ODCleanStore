package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {
		try {
			for (int i = 0; i < 1844; ++i) {
				final int id = i;

				new DataNormalizerImpl().transformNewGraph(new TransformedGraph() {

					@Override
					public String getGraphName() {
						// TODO Auto-generated method stub
						return "http://opendata.cz/data/namedGraph/" + id;
					}

					@Override
					public String getGraphId() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getMetadataGraphName() {
						// TODO Auto-generated method stub
						return null;
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
					public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
						// TODO Auto-generated method stub
						return new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba");
					}

					@Override
					public JDBCConnectionCredentials getCleanDatabaseCredentials() {
						// TODO Auto-generated method stub
						return new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
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
			}
		} catch (Exception e) {
			System.err.println("DNMain: " + e.getMessage());
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
	
	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		throw new TransformerException("Data normalization is supposed to be applied to new graphs.");
	}
	
	private void loadRules () throws DataNormalizationException {
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "{<a> <test> 'c'}",
				EnumRuleComponentType.RULE_COMPONENT_DELETE, "{} WHERE {}",
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "{<a> <b> ?o} WHERE {?s <test> ?o}",
				EnumRuleComponentType.RULE_COMPONENT_DELETE, "{<a> <test> 'c'}"
				));
	}

	private void applyRules () throws DataNormalizationException {
		try {
			getDirtyConnection();
			
			Iterator<Rule> i = rules.iterator();
			
			while (i.hasNext()) {
				Rule rule = i.next();

				getDirtyConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
				
				String[] components = rule.toString(inputGraph.getGraphName());

				for (int j = 0; j < components.length; ++j) {
					getDirtyConnection().execute(components[j]);
				}
				
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

	@Override
	public void shutdown() throws TransformerException {
	}
}
