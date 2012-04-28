package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;
import cz.cuni.mff.odcleanstore.engine.InputGraphState;


final class WorkingInputGraphState {

	private String _uuid;
	private InputGraphState _state;
	
	WorkingInputGraphState() {
	}
	
	String getUuid() {
		return _uuid;
	}

	InputGraphState getState() {
		return _state;
	}

	boolean convertToState(InputGraphState newState) {
		return false;
	}

	Collection<String> getAttachedGraphNames() {
		return null;
	}

	void addAttachedGraphName(String attachedGraphName) {
	}

	void delete() {
	}
}
