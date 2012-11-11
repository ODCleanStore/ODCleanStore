package cz.cuni.mff.odcleanstore.engine.inputws;

import cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

public abstract class SoapInsertMethodExecutor extends SoapMethodExecutor {

	private boolean isNext;
	private StringBuilder builder;

	@Override
	public void startElement(String uri, String localName) throws SoapMethodExecutorException {
		
		builder = null;

		if (!isNext && (!uri.equals("http://inputws.engine.odcleanstore.mff.cuni.cz/") || !localName.equals("insert"))) {
			String message = String.format("unknown method %s : %s", uri, localName);
			throw new SoapMethodExecutorException(message);
		}

		isNext = true;

		if (localName.equals("metadata")) {
			return;
		}

		builder = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SoapMethodExecutorException {
		if (builder != null) {
			builder.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName) throws SoapMethodExecutorException {
		if (!uri.isEmpty() || localName.equals("metadata")) {
			return;
		}

		onElement(localName, builder.toString());
	}

	protected abstract void onElement(String name, String content) throws InsertExecutorException;
	
	//
	// try {
	// insert.endParsing();
	// } catch (Exception e) {
	// error(e);
	// }
	// String payload = "<?xml version='1.0' encoding='UTF-8'?>"
	// + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""
	// + " xmlns:i=\"http://inputws.engine.odcleanstore.mff.cuni.cz/\">"
	// + "<s:Header/><s:Body><i:insertResponse/></s:Body></s:Envelope>";
	//
	// try {
	// request.SendResponse(HttpURLConnection.HTTP_OK, "text/xml", payload);
	// } catch (Exception e) {}
	// }
}
