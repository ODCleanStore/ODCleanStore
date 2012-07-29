package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.engine.InputGraphState;

/**
 *  @author Petr Jerman
 */
final class WorkingInputGraphStatus {

	private String _dbSchemaPrefix;
	private TransformedGraphImpl _workingTransformedGraphImpl;

	WorkingInputGraphStatus(String dbSchemaPrefix) throws Exception {
		_dbSchemaPrefix = dbSchemaPrefix;
	}

	String getWorkingTransformedGraphUuid() throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS" + " WHERE state='PROCESSING' OR state='PROCESSED' OR state ='PROPAGATED' OR state ='DELETING' OR state ='DIRTY'",
					_dbSchemaPrefix);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			if(resultSet.next()) {
				return resultSet.getString(1);
			}
			return null;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	int getGraphDbKeyId(String uuid) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select Id from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			resultSet.next();
			return resultSet.getInt(1);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	int getGraphPipelineId(String uuid) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select pipelineId from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			resultSet.next();
			return resultSet.getInt(1);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	Collection<String> getWorkingAttachedGraphNames() throws Exception {
		LinkedList<String> retVal = new LinkedList<String>();
		
		if (_workingTransformedGraphImpl == null) {
			return retVal;
		}
		
		int dbKeyId = _workingTransformedGraphImpl.getGraphDbKeyId();
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select name from %s.EN_WORKING_ADDED_GRAPHS WHERE graphId = %d", _dbSchemaPrefix, dbKeyId);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			while(resultSet.next()) {
				retVal.add(resultSet.getString(1));
			}
			return retVal;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	void deleteWorkingAttachedGraphNames() throws Exception {
		if (_workingTransformedGraphImpl == null) {
			return;
		}
		
		int dbKeyId = _workingTransformedGraphImpl.getGraphDbKeyId();
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Delete from %s.EN_WORKING_ADDED_GRAPHS HERE graphId = %d", _dbSchemaPrefix, dbKeyId);
			con.execute(sqlStatement);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void deleteGraphAndWorkingAttachedGraphNames(String uuid) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Delete from %s.EN_WORKING_ADDED_GRAPHS WHERE graphId = '%s'", _dbSchemaPrefix, uuid);
			con.execute(sqlStatement);
			sqlStatement = String.format("Delete from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			con.execute(sqlStatement);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	InputGraphState getState(String uuid) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select state from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			resultSet.next();
			return InputGraphState.valueOf(resultSet.getString(1));
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	void setState(String uuid, InputGraphState newState) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Update %s.EN_INPUT_GRAPHS SET state='%S' WHERE uuid='%s'", _dbSchemaPrefix, newState.toString(), uuid);
			con.execute(sqlStatement);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	String getNextProcessingGraphUuid() throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE state='PROCESSING'", _dbSchemaPrefix);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			if(resultSet.next()) {
				return resultSet.getString(1);
			}

			sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE state='IMPORTED'", _dbSchemaPrefix);
			resultSet = con.executeSelect(sqlStatement);
			if(resultSet.next()) {
				String uuid  = resultSet.getString(1);
				sqlStatement = String.format("Update %s.EN_INPUT_GRAPHS SET state='%S' WHERE uuid='%s'", _dbSchemaPrefix, InputGraphState.PROCESSING, uuid);
				con.execute(sqlStatement);
				con.commit();
				return uuid;
			}
			return null;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	synchronized void setWorkingTransformedGraph(TransformedGraphImpl transformedGraphImpl) {
		_workingTransformedGraphImpl = transformedGraphImpl;
	}

	synchronized void addAttachedGraphName(TransformedGraphImpl transformedGraphImpl, String attachedGraphName) throws Exception {
		if (transformedGraphImpl == null || transformedGraphImpl != _workingTransformedGraphImpl) {
			throw new NotWorkingTransformerException();
		}

		int dbKeyId = _workingTransformedGraphImpl.getGraphDbKeyId();
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format("Insert into %s.EN_WORKING_ADDED_GRAPHS(name, graphId) VALUES('%s', %d)", _dbSchemaPrefix, attachedGraphName, dbKeyId);
			con.execute(sqlStatement);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	synchronized void deleteGraph(TransformedGraphImpl transformedGraphImpl) throws Exception {
		if (transformedGraphImpl == null || transformedGraphImpl != _workingTransformedGraphImpl) {
			throw new NotWorkingTransformerException();
		}

		setState(transformedGraphImpl.getGraphId(), InputGraphState.DELETING);
	}

	static class NotWorkingTransformerException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
