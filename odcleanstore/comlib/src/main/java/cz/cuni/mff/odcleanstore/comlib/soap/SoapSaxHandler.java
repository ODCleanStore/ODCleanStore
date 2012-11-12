package cz.cuni.mff.odcleanstore.comlib.soap;

import java.net.URI;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import cz.cuni.mff.odcleanstore.comlib.io.EndableReader;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapExecutorException;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapMethodExecutorException;

class SoapSaxHandler extends DefaultHandler {

	private static final String HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";

	private SoapMethodExecutor soapMethodExecutor;
	private EndableReader reader;

	private enum State {
		START, BODY, FORWARD, FAULT, FAULTFORWARD, END
	};

	private int level;
	private State state;

	private StringBuilder currentBuilder;

	SoapSaxHandler(SoapMethodExecutor soapMethodExecutor, EndableReader reader) {
		this.soapMethodExecutor = soapMethodExecutor;
		this.reader = reader;

		level = 0;
		state = State.START;

		currentBuilder = null;
	}

	/**
	 * Analyze notification of each start element in SOAP message.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 *      org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		level++;
		switch (state) {
		case FORWARD:
		case FAULTFORWARD:
			try {
				soapMethodExecutor.startElement(uri, localName);
			} catch (SoapMethodExecutorException e) {
				throw new SAXException(e);
			}
			break;
		case START:
			if (level == 2 && uri.equals(HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE) && localName.equals("Body")) {
				state = State.BODY;
			}
			break;
		case BODY:
			if (uri.equals(HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE) && localName.equals("Fault")) {
				state = State.FAULT;
				soapMethodExecutor.setFault(true);
			} else {
				state = State.FORWARD;
				try {
					soapMethodExecutor.startElement(uri, localName);
				} catch (SoapMethodExecutorException e) {
					throw new SAXException(e);
				}
			}
			break;
		case FAULT:
			if (uri.equals("") && localName.equals("faultcode")) {
				currentBuilder = new StringBuilder();
			} else if (uri.equals("") && localName.equals("faultstring")) {
				currentBuilder = new StringBuilder();
			} else if (uri.equals("") && localName.equals("faulactor")) {
				currentBuilder = new StringBuilder();
			} else if (uri.equals("") && localName.equals("detail")) {
				state = State.FAULTFORWARD;
			} else {
				state = State.END;
			}
			break;
		}
	}

	/**
	 * Extract id, message and moreinfo of InsertException from SOAP.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (state == State.FORWARD || state == State.FAULTFORWARD) {
			try {
				soapMethodExecutor.characters(ch, start, length);
			} catch (SoapMethodExecutorException e) {
				throw new SAXException(e);
			}
		} else if (currentBuilder != null) {
			currentBuilder.append(ch, start, length);
		}
	}

	/**
	 * Remove node from analyzer stack and check if element is properly ended.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		level--;

		if (state == State.FORWARD) {
			if (level == 2) {
				state = State.END;
			}
			try {
				soapMethodExecutor.endElement(uri, localName);
			} catch (SoapMethodExecutorException e) {
				throw new SAXException(e);
			}
		} else if (state == State.FAULTFORWARD) {
			if (level == 3) {
				state = State.END;
			}
			try {
				soapMethodExecutor.endElement(uri, localName);
			} catch (SoapMethodExecutorException e) {
				throw new SAXException(e);
			}
		} else if (state == State.BODY) {
			throw new SAXException(new SoapExecutorException("cannot find {} method"));
		} else if (state == State.FAULT) {
			if (uri.equals("") && localName.equals("faultcode")) {
				soapMethodExecutor.setFaultCode(currentBuilder.toString());
			} else if (uri.equals("") && localName.equals("faultstring")) {
				soapMethodExecutor.setFaultString(currentBuilder.toString());
			} else if (uri.equals("") && localName.equals("faulactor")) {
				try {
					String faultActor = currentBuilder.toString();
					if (!faultActor.isEmpty()) {
						soapMethodExecutor.setFaultActor(new URI(faultActor.toString()));
					}
				} catch (Exception e) {
					// do nothing - URI is controlled by SAX Parser xsd
				}
			}
		}

		currentBuilder = null;

		if (level == 0) {
			reader.setForceEOF();
			try {
				soapMethodExecutor.endParsing();
			} catch (SoapMethodExecutorException e) {
				throw new SAXException(e);
			}
		}
	}

	/**
	 * Throw SAXException.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		throw new SAXException(e);
	}
}
