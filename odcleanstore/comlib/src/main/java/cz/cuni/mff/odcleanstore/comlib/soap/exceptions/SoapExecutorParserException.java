package cz.cuni.mff.odcleanstore.comlib.soap.exceptions;


public class SoapExecutorParserException extends SoapExecutorException {

	private static final long serialVersionUID = 329088736236567898L;

	public SoapExecutorParserException(String message) {
		super(message);
	}

	@Override
	protected String getFaultCode() {
		return "S:Client";
	}
}
