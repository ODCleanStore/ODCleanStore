package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.IOException;

import cz.cuni.mff.odcleanstore.comlib.soap.SoapWriter;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

/**
 * A exception arising from InputWS WebService.
 * 
 * 
 * @author Petr Jerman
 */
public final class InsertExecutorException extends SoapMethodExecutorException {

	private static final long serialVersionUID = -4034801255210653582L;

	private InputWSErrorEnumeration id;
	private String moreInfo;

	public InsertExecutorException(Throwable e) {
		super(InputWSErrorEnumeration.getMessage(InputWSErrorEnumeration.FATAL_ERROR));
		this.id = InputWSErrorEnumeration.FATAL_ERROR;
	}

	public InsertExecutorException(InputWSErrorEnumeration id, String moreInfo) {
		super(InputWSErrorEnumeration.getMessage(id));
		this.id = id;
		this.moreInfo = moreInfo;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + (moreInfo != null && !moreInfo.isEmpty() ? " - " + moreInfo : "");
	}

	@Override
	protected String getFaultCode() {
		return id == InputWSErrorEnumeration.FATAL_ERROR ? "S:Server" : "S:Client";
	}

	@Override
	protected void writeDetail(SoapWriter soapWriter) throws IOException {
	
		soapWriter.write("<detail><ns2:InsertException xmlns:ns2=\"http://inputws.engine.odcleanstore.mff.cuni.cz/\">");
		soapWriter.writeSimpleXMLElement("id", id.ordinal());
		soapWriter.writeSimpleXMLElement("message", InputWSErrorEnumeration.getMessage(id));
		soapWriter.writeSimpleXMLElement("moreInfo", moreInfo);
		soapWriter.write("</ns2:InsertException></detail>");
	}
}
