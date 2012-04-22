package cz.cuni.mff.odcleanstore.engine.ws.scraper;


public final class ImportingInputGraphStates {
	
	ImportingInputGraphStates() {
	}
	
	public synchronized String[] getAllImportingGraphUuids() {
		return null;
	}
	
	public synchronized void deleteAllImportingGraphUuids() {
	}
	
	public synchronized String beginImportSession(String graphUuid, Runnable interruptNotifyTask) {
		return null;
	}
	
	public synchronized boolean commitImportSession(String importUuid) {
		return false;
	}
	
	public synchronized void revertImportSession(String importUuid) {
	}
}
