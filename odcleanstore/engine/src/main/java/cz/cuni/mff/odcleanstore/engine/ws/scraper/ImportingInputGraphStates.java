package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.common.Utils;

@SuppressWarnings("serial")
public final class ImportingInputGraphStates {

	class ServiceBusyException extends Exception {
	}

	class DuplicatedUuid extends Exception {
	}

	class NoActiveImportSession extends Exception {
	}

	private String _dbSchemaPrefix = "DB.FRONTEND";
	private String _actualImportingGraphUuid;

	ImportingInputGraphStates() {
	}

	Collection<String> getAllImportingGraphUuids() throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE state='IMPORTING'", _dbSchemaPrefix);
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
			String sqlStatement = String.format("Delete from %s.EN_INPUT_GRAPHS WHERE state='IMPORTING'", _dbSchemaPrefix);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized String beginImportSession(String graphUuid, Runnable interruptNotifyTask) throws Exception {

		if (_actualImportingGraphUuid != null) {
			throw new ServiceBusyException();
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();

			String sqlStatement = String.format("Select uuid from %s.EN_INPUT_GRAPHS WHERE uuid='%s'", _dbSchemaPrefix, graphUuid);
			if (sva.getRowFromSqlStatement(sqlStatement).size() != 0) {
				throw new DuplicatedUuid();
			}

			sqlStatement = String.format("Insert into %s.EN_INPUT_GRAPHS(uuid, state) VALUES('%s', '%s')", _dbSchemaPrefix, graphUuid, InputGraphState.IMPORTING);
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
			String sqlStatement = String.format("Update %s.EN_INPUT_GRAPHS SET state='%s' WHERE uuid='%s'", _dbSchemaPrefix, InputGraphState.IMPORTED, importUuid);
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
			String sqlStatement = String.format("Delete from %s.EN_INPUT_GRAPHS(uuid) VALUES('%s')", _dbSchemaPrefix, importUuid);
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
