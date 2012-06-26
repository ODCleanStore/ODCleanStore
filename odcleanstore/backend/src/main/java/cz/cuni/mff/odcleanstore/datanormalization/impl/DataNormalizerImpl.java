package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class DataNormalizerImpl implements DataNormalizer {
	
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
		transformExistingGraph(inputGraph, context);

	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
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
	
	private void loadRules () throws DataNormalizationException {
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "{}",
				EnumRuleComponentType.RULE_COMPONENT_DELETE, "{} WHERE {}",
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "{}"
				));
	}

	private void applyRules () throws DataNormalizationException {
		try {
			getDirtyConnection();
			
			Iterator<Rule> i = rules.iterator();
			
			while (i.hasNext()) {
				Rule rule = i.next();

				getDirtyConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);

				getDirtyConnection().execute(rule.toString(inputGraph.getGraphName()));
				
				getDirtyConnection().commit();
			}
		} catch (ConnectionException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (QueryException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (SQLException e) {
			throw new DataNormalizationException(e.getMessage());
		}

		throw new DataNormalizationException("Not yet implemented");
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
