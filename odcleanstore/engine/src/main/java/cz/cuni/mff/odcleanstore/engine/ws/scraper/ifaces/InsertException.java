package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

public class InsertException extends Exception {

	private static final long serialVersionUID = 1L;

	public InsertException(InsertExceptionStatus status) {
		super(status.toString());
	}
}
