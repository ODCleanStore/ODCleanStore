package cz.cuni.mff.odcleanstore.comlib.soap;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.comlib.io.EndableReader;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapExecutorException;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapExecutorParserException;

public class SoapExecutor {
	private static final String ENVELOPE_XSD_RESOURCE_PATH = "/envelope.xsd";
	private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	private static Source envelopeSchema = null;
	private static Object envelopeSchemaLock = new Object();

	private SAXParserFactory saxParserFactory;

	public SoapExecutor(@SuppressWarnings("rawtypes") Class classWithResources, String typesSchemaResourceName)
			throws SAXException {
		InputStream typesSchema = classWithResources.getResourceAsStream(typesSchemaResourceName);
		createParser(typesSchema);
	}

	public SoapExecutor(InputStream typesSchema) throws SAXException {
		createParser(typesSchema);
	}

	public void parse(Reader reader, SoapMethodExecutor handler) throws SoapExecutorException {

		SAXParser saxParser = null;
		EndableReader endableReader = null;

		try {
			saxParser = saxParserFactory.newSAXParser();
			endableReader = new EndableReader(reader);
		} catch (Exception e) {
			throw new SoapExecutorException(e.getMessage());
		}

		try {
			saxParser.parse(new InputSource(endableReader), new SoapSaxHandler(handler, endableReader));
		} catch (SAXException e) {
			Throwable cause = e.getCause();
			if (cause instanceof SoapExecutorException) {
				throw (SoapExecutorException) cause;
			} else {
				throw new SoapExecutorParserException(e.getMessage());
			}
		} catch (Exception e) {
			throw new SoapExecutorException(e.getMessage());
		}
	}

	private void createParser(InputStream typesSchema) throws SAXException {

		synchronized (envelopeSchemaLock) {
			if (envelopeSchema == null) {
				InputStream envelopeStream = this.getClass().getResourceAsStream(ENVELOPE_XSD_RESOURCE_PATH);
				envelopeSchema = new StreamSource(envelopeStream);
			}
		}

		saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);

		String schemaLang = HTTP_WWW_W3_ORG_2001_XML_SCHEMA;
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

		Source[] sources = new Source[] { new StreamSource(typesSchema), envelopeSchema };

		Schema schema = factory.newSchema(sources);
		saxParserFactory.setSchema(schema);
	}
}
