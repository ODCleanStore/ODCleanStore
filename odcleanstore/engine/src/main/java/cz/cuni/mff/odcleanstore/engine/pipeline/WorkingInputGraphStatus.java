package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

final class WorkingInputGraphStatus {

	private static final String NOT_WORKING_TRANSFORMER = "Operation is permitted only for working transformer";

	private TransformedGraphImpl _workingTransformedGraphImpl;

	WorkingInputGraphStatus(PipelineService pipelineService) {
		if (pipelineService == null) {
			throw new IllegalArgumentException();
		}
	}

	synchronized Collection<String> getWorkingTransformedGraphUuids() throws TransformerException {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.CreateDefaultDbaConnection();
			Collection<String[]> rows = sva.executeSqlStatement("Select uuid from DB.FRONTEND.EN_INPUT_GRAPHS"
					+ " WHERE state='PROCESSING' OR state='PROCESSED' OR state ='PROPAGATED' OR state ='FINISHED' OR state ='DELETING' OR state ='DIRTY'");
			Collection<String> retVal = new LinkedList<String>();
			for (String[] row : rows) {
				retVal.add(row[0]);
			}
			return retVal;
		} catch (Exception e) {
			throw new TransformerException(e);
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	synchronized void setWorkingTransformedGraph(TransformedGraphImpl transformedGraphImpl) {
		_workingTransformedGraphImpl = transformedGraphImpl;
	}

	synchronized InputGraphState getState(TransformedGraphImpl transformedGraphImpl) {

		return InputGraphState.WRONG;
	}

	synchronized boolean convertToState(TransformedGraphImpl transformedGraphImpl, InputGraphState newState) {
		return false;
	}

	synchronized LinkedList<String> getAttachedGraphNames(TransformedGraphImpl transformedGraphImpl) {
		return null;
	}

	synchronized void addAttachedGraphName(TransformedGraphImpl transformedGraphImpl, String attachedGraphName)  throws TransformerException {
	}

	synchronized void delete(TransformedGraphImpl transformedGraphImpl)  throws TransformerException {
	}
}
