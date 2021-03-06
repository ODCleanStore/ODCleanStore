package cz.cuni.mff.odcleanstore.comlib.soap.exceptions;

/**
 * Client soap method executor exception.
 * 
 * @author Petr Jerman
 */
public class SoapMethodExecutorException extends SoapExecutorException  {

	private static final long serialVersionUID = 3772600171412892268L;

	public SoapMethodExecutorException(String message) {
		super(message);
	}

	@Override
	protected String getFaultCode() {
		return "S:Client";
	}
}
