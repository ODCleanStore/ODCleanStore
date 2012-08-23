package cz.cuni.mff.odcleanstore.engine.inputws;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.engine.db.model.GraphStates;

/**
 *  @author Petr Jerman
 */
@SuppressWarnings("serial")
public final class InputGraphStatus {

	class ServiceBusyException extends Exception {
	}

	class DuplicatedUuid extends Exception {
	}
	
	class UnknownPipelineName extends Exception {
	}
	
	class UnknownPipelineDefaultName extends Exception {
	}

	class NoActiveImportSession extends Exception {
	}

	private final static String DB_SCHEMA_PREFIX = "DB.ODCLEANSTORE";
	private final static String DEFAULT_PIPELINE_NAME = "Dirty";
	
	private String _actualImportingGraphUuid;
	
	InputGraphStatus() {
	}

	Collection<String> getAllImportingGraphUuids() throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format(Locale.ROOT, "Select uuid from %s.EN_INPUT_GRAPHS WHERE stateId=%d", DB_SCHEMA_PREFIX, GraphStates.IMPORTING.toId());
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			LinkedList<String> retVal = new LinkedList<String>();
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

	void deleteAllImportingGraphUuids() throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format(Locale.ROOT, "Delete from %s.EN_INPUT_GRAPHS WHERE stateId=%d", DB_SCHEMA_PREFIX, GraphStates.IMPORTING.toId());
			con.execute(sqlStatement);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	synchronized String beginImportSession(String graphUuid, String pipelineName, Runnable interruptNotifyTask) throws Exception {
		if (_actualImportingGraphUuid != null) {
			throw new ServiceBusyException();
		}
		VirtuosoConnectionWrapper con = null; 
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			
			String sqlStatement = String.format(Locale.ROOT, "Select uuid from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", DB_SCHEMA_PREFIX, graphUuid);
			WrappedResultSet resultSet = con.executeSelect(sqlStatement);
			if (resultSet.next()) {
				throw new DuplicatedUuid();
			}
			
			if (pipelineName == null || pipelineName.isEmpty())	{
				sqlStatement = String.format(Locale.ROOT, "Select id from %s.PIPELINES WHERE label='%s'", DB_SCHEMA_PREFIX, DEFAULT_PIPELINE_NAME);
			}
			else {
				sqlStatement = String.format(Locale.ROOT, "Select id from %s.PIPELINES WHERE label='%s'", DB_SCHEMA_PREFIX, pipelineName);
			}
			resultSet = con.executeSelect(sqlStatement);
			if (!resultSet.next()) {
				throw pipelineName == null || pipelineName.isEmpty() ? new UnknownPipelineDefaultName(): new UnknownPipelineName();
			}
			String pipelineId = resultSet.getString(1);
			
			sqlStatement = String.format(Locale.ROOT, "Insert into %s.EN_INPUT_GRAPHS(uuid, pipelineId, stateId) VALUES('%s', '%s', %d)", DB_SCHEMA_PREFIX, graphUuid, pipelineId , GraphStates.IMPORTING.toId());
			con.execute(sqlStatement);
			con.commit();

			_actualImportingGraphUuid = graphUuid;
			return graphUuid;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	synchronized void commitImportSession(String importUuid) throws Exception {
		if (importUuid == null || _actualImportingGraphUuid != importUuid) {
			throw new NoActiveImportSession();
		}

		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format(Locale.ROOT, "Update %s.EN_INPUT_GRAPHS SET stateId=%d WHERE uuid='%s'", DB_SCHEMA_PREFIX, GraphStates.QUEUED.toId(), importUuid);
			con.execute(sqlStatement);
			con.commit();
			_actualImportingGraphUuid = null;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	synchronized void revertImportSession(String importUuid) throws Exception {
		if (importUuid == null || _actualImportingGraphUuid != importUuid) {
			throw new NoActiveImportSession();
		}

		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			String sqlStatement = String.format(Locale.ROOT, "Delete from %s.EN_INPUT_GRAPHS(uuid) VALUES('%s')", DB_SCHEMA_PREFIX, importUuid);
			con.execute(sqlStatement);
			con.commit();
			_actualImportingGraphUuid = null;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
}
