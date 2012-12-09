package cz.cuni.mff.odcleanstore.engine;

/**
 *  @author Petr Jerman
 */
public enum ServiceState {
    NEW, INITIALIZING, INITIALIZED, RUNNING, STOP_PENDING, STOPPED, CRASHED;

    /**
     * @return new or initializing
     */
    public boolean isNewOrInitializing() {
        return this == NEW || this == INITIALIZING;
    }

    
    /**
     * @return service stopped or crashed 
     */
    public boolean isEnded() {
        return this == STOPPED || this == CRASHED;
    }

    /**
     * @return shutdown not initiated
     */
    public boolean isForInitiateShutdown() {
        return this != STOP_PENDING && this != STOPPED;
    }
}
