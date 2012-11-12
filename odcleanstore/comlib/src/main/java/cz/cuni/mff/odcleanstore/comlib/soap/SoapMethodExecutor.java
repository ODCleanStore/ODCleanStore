package cz.cuni.mff.odcleanstore.comlib.soap;

import java.net.URI;

import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

public abstract class SoapMethodExecutor {
	private boolean isFault;
	private String faultCode;
	private String faultString;
	private URI faultActor;

	public boolean isFault() {
		return isFault;
	}

	public String getFaultCode() {
		return faultCode;
	}

	public String getFaultString() {
		return faultString;
	}

	public URI getFaultActor() {
		return faultActor;
	}

	void setFault(boolean isFault) {
		this.isFault = isFault;
	}

	void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	void setFaultString(String faultString) {
		this.faultString = faultString;
	}

	void setFaultActor(URI faultActor) {
		this.faultActor = faultActor;
	}

	protected SoapMethodExecutor() {
	}

	public abstract void startElement(String uri, String localName) throws SoapMethodExecutorException;

	public abstract void characters(char[] ch, int start, int length) throws SoapMethodExecutorException;

	public abstract void endElement(String uri, String localName) throws SoapMethodExecutorException;
	
	public abstract void endParsing() throws SoapMethodExecutorException;
}
