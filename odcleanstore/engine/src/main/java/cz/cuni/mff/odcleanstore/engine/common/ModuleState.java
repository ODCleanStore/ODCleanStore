package cz.cuni.mff.odcleanstore.engine.common;

/**
 *  @author Petr Jerman
 */
public enum ModuleState {
	NEW, INITIALIZING, INITIALIZED, RUNNING, PAUSE_PENDING, PAUSED, CONTINUE_PENDING, STOP_PENDING, STOPPED, CRASHED
}
