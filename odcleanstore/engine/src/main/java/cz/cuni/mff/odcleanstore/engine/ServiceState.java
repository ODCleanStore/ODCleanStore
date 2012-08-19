package cz.cuni.mff.odcleanstore.engine;

/**
 *  @author Petr Jerman
 */
public enum ServiceState {
	NEW, INITIALIZING, INITIALIZED, RUNNING, STOP_PENDING, STOPPED, CRASHED;

	public boolean isNewOrInitializing() {
		return this == NEW || this == INITIALIZING;
	}

	public boolean isEnded() {
		return this == STOPPED || this == CRASHED;
	}

	public boolean isForInitiateShutdown() {
		return this != STOP_PENDING && this != STOPPED;
	}
}
