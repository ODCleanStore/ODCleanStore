package cz.cuni.mff.odcleanstore.comlib.soap.exceptions;

public class SoapExecutorHeaderException extends SoapExecutorException {

	private static final long serialVersionUID = -306040091260807623L;

	public SoapExecutorHeaderException(String message) {
		super(message);
	}
	
	@Override
	protected String getFaultCode() {
		return "S:MustUnderstand";
	}
}
