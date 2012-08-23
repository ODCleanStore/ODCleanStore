package cz.cuni.mff.odcleanstore.engine.inputws;

import java.util.Collection;
import java.util.Locale;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.common.Utils;

/**
 *  @author Petr Jerman
 */
@SuppressWarnings("serial")
public final class ImportingInputGraphStates {

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
	
	ImportingInputGraphStates() {
	}

	Collection<String> getAllImportingGraphUuids() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format(Locale.ROOT, "Select uuid from %s.EN_INPUT_GRAPHS WHERE state='IMPORTING'", DB_SCHEMA_PREFIX);
			Collection<String[]> rows = sva.getRowFromSqlStatement(sqlStatement);
			return Utils.selectColumn(rows, 0);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	void deleteAllImportingGraphUuids() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format(Locale.ROOT, "Delete from %s.EN_INPUT_GRAPHS WHERE state='IMPORTING'", DB_SCHEMA_PREFIX);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized String beginImportSession(String graphUuid, String pipelineName, Runnable interruptNotifyTask) throws Exception {

		if (_actualImportingGraphUuid != null) {
			throw new ServiceBusyException();
		}
		
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();

			String sqlStatement = String.format(Locale.ROOT, "Select uuid from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", DB_SCHEMA_PREFIX, graphUuid);
			if (sva.getRowFromSqlStatement(sqlStatement).size() != 0) {
				throw new DuplicatedUuid();
			}
			
			if (pipelineName == null || pipelineName.isEmpty())	{
				sqlStatement = String.format(Locale.ROOT, "Select id from %s.PIPELINES WHERE label='%s'", DB_SCHEMA_PREFIX, DEFAULT_PIPELINE_NAME);
			}
			else {
				sqlStatement = String.format(Locale.ROOT, "Select id from %s.PIPELINES WHERE label='%s'", DB_SCHEMA_PREFIX, pipelineName);
			}
			
			Collection<String[]> pipelineRows = sva.getRowFromSqlStatement(sqlStatement);
			
			if (pipelineRows.isEmpty()) {
				throw pipelineName == null || pipelineName.isEmpty() ? new UnknownPipelineDefaultName(): new UnknownPipelineName();
			}
			
			String pipelineId = pipelineRows.iterator().next()[0];
			sqlStatement = String.format(Locale.ROOT, "Insert into %s.EN_INPUT_GRAPHS(uuid, pipelineId, state) VALUES('%s', '%s', '%s')", DB_SCHEMA_PREFIX, graphUuid, pipelineId , InputGraphState.IMPORTING);
			sva.executeStatement(sqlStatement);
			sva.commit();

			_actualImportingGraphUuid = graphUuid;
			return graphUuid;
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized void commitImportSession(String importUuid) throws Exception {
		if (importUuid == null || _actualImportingGraphUuid != importUuid) {
			throw new NoActiveImportSession();
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format(Locale.ROOT, "Update %s.EN_INPUT_GRAPHS SET state='%s' WHERE uuid='%s'", DB_SCHEMA_PREFIX, InputGraphState.IMPORTED, importUuid);
			sva.executeStatement(sqlStatement);
			sva.commit();
			_actualImportingGraphUuid = null;
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized void revertImportSession(String importUuid) throws Exception {
		if (importUuid == null || _actualImportingGraphUuid != importUuid) {
			throw new NoActiveImportSession();
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format(Locale.ROOT, "Delete from %s.EN_INPUT_GRAPHS(uuid) VALUES('%s')", DB_SCHEMA_PREFIX, importUuid);
			sva.executeStatement(sqlStatement);
			sva.commit();
			_actualImportingGraphUuid = null;
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
}
