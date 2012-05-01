package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.common.Utils;

final class WorkingInputGraphStatus {

	private String _dbSchemaPrefix;
	private TransformedGraphImpl _workingTransformedGraphImpl;

	WorkingInputGraphStatus(String dbSchemaPrefix) throws Exception {
		_dbSchemaPrefix = dbSchemaPrefix;
	}

	String getWorkingTransformedGraphUuid() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			Collection<String[]> rows;
			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS" + " WHERE state='PROCESSING' OR state='PROCESSED' OR state ='PROPAGATED' OR state ='DELETING' OR state ='DIRTY'",
					_dbSchemaPrefix);
			rows = sva.getRowFromSqlStatement(sqlStatement);
			return Utils.selectScalar(rows);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
	
	Collection<String> getWorkingAttachedGraphNames() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			Collection<String[]> rows;
			String sqlStatement = String.format("Select name from %s.EN_WORKING_ADDED_GRAPHS", _dbSchemaPrefix);
			rows = sva.getRowFromSqlStatement(sqlStatement);
			return Utils.selectColumn(rows, 0);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
	
	void deleteWorkingAttachedGraphNames() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Delete from %s.EN_WORKING_ADDED_GRAPHS", _dbSchemaPrefix);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteGraphAndWorkingAttachedGraphNames(String uuid) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Delete from %s.EN_WORKING_ADDED_GRAPHS", _dbSchemaPrefix);
			sva.executeStatement(sqlStatement);
			sqlStatement = String.format("Delete from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	InputGraphState getState(String uuid) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Select state from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, uuid);
			Collection<String[]> rows = sva.getRowFromSqlStatement(sqlStatement);
			return InputGraphState.valueOf(Utils.selectScalar(rows));
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void setState(String uuid, InputGraphState newState) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Update %s.EN_INPUT_GRAPHS SET state='%S' WHERE uuid='%s'", _dbSchemaPrefix, newState.toString(), uuid);
			sva.getRowFromSqlStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	String getNextProcessingGraphUuid() throws Exception {
		SimpleVirtuosoAccess sva = null;
		String uuid = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			Collection<String[]> rows;
			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE state='PROCESSING'", _dbSchemaPrefix);
			rows = sva.getRowFromSqlStatement(sqlStatement);
			uuid = Utils.selectScalar(rows);
			if (uuid != null) {
				return uuid;
			}

			sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE state='IMPORTED'", _dbSchemaPrefix, uuid);
			rows = sva.getRowFromSqlStatement(sqlStatement);
			uuid = Utils.selectScalar(rows);
			if (uuid != null) {
				sqlStatement = String.format("Update %s.EN_INPUT_GRAPHS SET state='%S' WHERE uuid='%s'", _dbSchemaPrefix, InputGraphState.PROCESSING, uuid);
				sva.getRowFromSqlStatement(sqlStatement);
				sva.commit();
				return uuid;
			}

			return null;
		} finally {
			if (sva != null) {
				sva.close();
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

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Insert into %s.EN_WORKING_ADDED_GRAPHS(name) VALUES('%s')", _dbSchemaPrefix, attachedGraphName);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
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
