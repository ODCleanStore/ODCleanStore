package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.xml.ws.WebFault;

@WebFault(name = "InsertException", targetNamespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/")
public class InsertException_Exception extends Exception {

	private static final long serialVersionUID = 1L;

	private InsertException faultInfo;

	public InsertException_Exception(String message, InsertException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public InsertException_Exception(String message, InsertException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public InsertException getFaultInfo() {
		return faultInfo;
	}

}