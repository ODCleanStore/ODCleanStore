package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SoapHttpReader is helper class for reading Soap Http request or response.
 * 
 * @author Petr Jerman
 */
public class SoapHttpReader extends HttpReader {

	/**
     * Create object for reading Soap Http request or response.
     * 
     * @param stream inputstream with http request or response. 
	 * @throws UnsupportedEncodingException 
     */
	public SoapHttpReader(InputStream stream) throws UnsupportedEncodingException {
		super(stream);
	}
	
	/**
	 * Read SOAP body and parse it to SAX DefaultHandler.
	 * 
	 * @param handler SAX DefaultHandler for receiving XML elements
	 * @throws IOException
	 */
	public void readSoapBody(DefaultHandler handler) throws IOException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setFeature("http://xml.org/sax/features/namespaces", true);
			SAXParser sp = spf.newSAXParser();
			sp.parse(new InputSource(reader), handler);
		}catch(SAXException e) {
			throw new IOException(e);
		}catch(ParserConfigurationException e) {
			throw new IOException(e);
		}catch (IOException e) {
			throw e;
		}	
	}
}
