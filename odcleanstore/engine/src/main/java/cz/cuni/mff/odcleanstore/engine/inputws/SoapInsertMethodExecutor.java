package cz.cuni.mff.odcleanstore.engine.inputws;

import cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

/**
 * Class for parsing insert inputws soap message.
 * Descendant of that class must implement only onElement method.
 * 
 *  @author Petr Jerman
 */
public abstract class SoapInsertMethodExecutor extends SoapMethodExecutor {

	private boolean isNext;
	private StringBuilder builder;

	/**
	 * @see cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor#startElement(java.lang.String, java.lang.String)
	 */
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

	/**
	 * @see cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SoapMethodExecutorException {
		if (builder != null) {
			builder.append(ch, start, length);
		}
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.comlib.soap.SoapMethodExecutor#endElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName) throws SoapMethodExecutorException {
		if (!uri.isEmpty() || localName.equals("metadata")) {
			return;
		}

		onElement(localName, builder.toString());
	}

	/**
	 * For descendant, text for element arrived.
	 * 
	 * @param name name of element
	 * @param content text of element
	 * @throws InsertExecutorException
	 */
	protected abstract void onElement(String name, String content) throws InsertExecutorException;
}
