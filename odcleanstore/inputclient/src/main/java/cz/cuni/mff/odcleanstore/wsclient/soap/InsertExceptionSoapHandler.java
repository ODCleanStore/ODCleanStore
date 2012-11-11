package cz.cuni.mff.odcleanstore.wsclient.soap;

import cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

/**
 * Class for reading InsertException from SOAP fault message.
 * 
 * @author Petr Jerman
 * 
 */
class InsertExceptionSoapHandler extends SoapMethodExecutor {

	private static final String HTTP_INPUTWS_ENGINE_ODCLEANSTORE_MFF_CUNI_CZ = "http://inputws.engine.odcleanstore.mff.cuni.cz/";

	private boolean isNext;
	private StringBuilder id, message, moreInfo, currentBuilder;

	/**
	 * Create InsertExceptionParser object for reading InsertException from SOAP fault message.
	 */
	InsertExceptionSoapHandler() {
		id = new StringBuilder();
		message = new StringBuilder();
		moreInfo = new StringBuilder();
	}

	/**
	 * @return recognized id member of InsertException
	 */
	public int getId() {
		try {
			return Integer.parseInt(id.toString());
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @return recognized message member of InsertException
	 */
	public String getMessage() {
		return message.toString();
	}

	/**
	 * @return recognized moreInfo member of InsertException
	 */
	public String getMoreInfo() {
		return moreInfo.toString();
	}

	@Override
	public void startElement(String uri, String localName) throws SoapMethodExecutorException {
		if (!isNext && !uri.equals(HTTP_INPUTWS_ENGINE_ODCLEANSTORE_MFF_CUNI_CZ) && !localName.equals("InsertException")) {
			String message = String.format("unknown method %s : %s", uri, localName);
			throw new SoapMethodExecutorException(message);
		}

		isNext = true;

		if (uri.equals("")) {
			if (localName.equals("id")) {
				currentBuilder = id;
			} else if (localName.equals("message")) {
				currentBuilder = message;
			} else if (localName.equals("moreInfo")) {
				currentBuilder = moreInfo;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SoapMethodExecutorException {
		if (currentBuilder != null) {
			currentBuilder.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName) throws SoapMethodExecutorException {
		currentBuilder = null;
	}

	@Override
	public void endParsing() throws SoapMethodExecutorException {
		// do nothing
	}
}
