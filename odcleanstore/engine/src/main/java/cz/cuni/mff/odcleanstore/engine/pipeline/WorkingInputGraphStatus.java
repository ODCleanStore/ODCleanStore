package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

final class WorkingInputGraphStatus {

	private static final String NOT_WORKING_TRANSFORMER = "Operation is permitted only for working transformer";

	private TransformedGraphImpl _workingTransformedGraphImpl;

	WorkingInputGraphStatus(PipelineService pipelineService) {
		if (pipelineService == null) {
			throw new IllegalArgumentException();
		}
	}

	synchronized Collection<String> getWorkingTransformedGraphUuids() throws TransformedGraphException {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.CreateDefaultDbaConnection();
			Collection<String[]> rows = sva.executeSqlStatement("Select uuid from DB.FRONTEND.EN_INPUT_GRAPHS"
					+ " WHERE state='PROCESSING' OR state='PROCESSED' OR state ='PROPAGATED' OR state ='DELETING' OR state ='DIRTY'");
			return Utils.selectColumn(rows, 0);
		} catch (Exception e) {
			throw new TransformedGraphException(e);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized void setWorkingTransformedGraph(TransformedGraphImpl transformedGraphImpl) {
		_workingTransformedGraphImpl = transformedGraphImpl;
	}

	synchronized InputGraphState getState(TransformedGraphImpl transformedGraphImpl) throws TransformedGraphException {
		if (transformedGraphImpl != _workingTransformedGraphImpl) {
			throw new TransformedGraphException(NOT_WORKING_TRANSFORMER);
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.CreateDefaultDbaConnection();
			String sqlstatement = String.format("Select state from DB.FRONTEND.EN_INPUT_GRAPHS WHERE uuid= %0", transformedGraphImpl.getGraphId());
			Collection<String[]> rows = sva.executeSqlStatement(sqlstatement);
			
			// Utils.selectColumn(rows, 0);

		} catch (Exception e) {
			throw new TransformedGraphException(e);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
		return InputGraphState.WRONG;
	}

	synchronized boolean convertToState(TransformedGraphImpl transformedGraphImpl, InputGraphState newState) {
		return false;
	}

	synchronized Collection<String> getAttachedGraphNames(TransformedGraphImpl transformedGraphImpl) throws TransformedGraphException {
		if (transformedGraphImpl != _workingTransformedGraphImpl) {
			throw new TransformedGraphException(NOT_WORKING_TRANSFORMER);
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.CreateDefaultDbaConnection();
			Collection<String[]> rows = sva.executeSqlStatement("Select name from DB.FRONTEND.EN_WORKING_ADDED_GRAPHS");
			return Utils.selectColumn(rows, 0);

		} catch (Exception e) {
			throw new TransformedGraphException(e);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized void addAttachedGraphName(TransformedGraphImpl transformedGraphImpl, String attachedGraphName) throws TransformedGraphException {
		if (transformedGraphImpl != _workingTransformedGraphImpl) {
			throw new TransformedGraphException(NOT_WORKING_TRANSFORMER);
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.CreateDefaultDbaConnection();
			Collection<String[]> rows = sva.executeSqlStatement("Select name from DB.FRONTEND.EN_WORKING_ADDED_GRAPHS");

		} catch (Exception e) {
			throw new TransformedGraphException(e);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}		
	}

	synchronized void delete(TransformedGraphImpl transformedGraphImpl) throws TransformedGraphException {
	}
}
