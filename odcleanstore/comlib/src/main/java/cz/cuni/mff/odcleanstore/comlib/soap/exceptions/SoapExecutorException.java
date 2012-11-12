package cz.cuni.mff.odcleanstore.comlib.soap.exceptions;

import java.io.IOException;
import java.io.StringWriter;

import cz.cuni.mff.odcleanstore.comlib.soap.SoapWriter;

public class SoapExecutorException extends Exception {

	private static final long serialVersionUID = -7289298224322006665L;

	public SoapExecutorException(String message) {
		super(message);
	}
	
	private static final String PROLOG =
			"<?xml version='1.0' encoding='UTF-8'?>"
			+ "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<S:Body>"
			+ "<S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">";
	
	private static final String EPILOG =
			"</S:Fault></S:Body></S:Envelope>";
	
	private static final String INTERNALSERVERERROR =
			PROLOG
			+ "<faultcode>S:Server</faultcode>"
			+ "<faultstring>Internal server error</faultstring>"
			+ EPILOG;
 	
	public final String getSoapMessage() {
		try {
			StringWriter sw = new StringWriter();
			SoapWriter soapWriter = new SoapWriter(sw);
			soapWriter.write(PROLOG);
			soapWriter.writeSimpleXMLElement("faultcode", getFaultCode());
			soapWriter.writeSimpleXMLElement("faultstring", getMessage());
			writeDetail(soapWriter);
			soapWriter.write(EPILOG);
			soapWriter.flush();
			return sw.toString();
		} catch (IOException e) {
			return INTERNALSERVERERROR;
		}
	}
	
	protected String getFaultCode() {
		return "S:Server";
	}
	
	protected void writeDetail(SoapWriter soapWriter) throws IOException {
	}
}	
	
	
