package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.UUID;

/**
 * SoapHttpWriter is helper class for writing Soap Http request or response.
 * 
 * @author Petr Jerman
 */
public class SoapHttpWriter extends HttpWriter {

	private static final int BLOCK_SIZE = 524288;
	
	/**
     * Create object for writing Soap Http request or response.
     * 
     * @param stream outputstream for soap http request or response writing.
	 * @throws UnsupportedEncodingException 
     */
	public SoapHttpWriter(OutputStream stream) throws UnsupportedEncodingException {
		super(stream);
	}
	
	/**
	 * Write XML element with elementName and escaped text.
	 * 
	 * @param elementName XML element name
	 * @param text element text
	 * @throws IOException
	 */
	public void writeSimpleXMLElement(String elementName, String text) throws IOException {
		writer.write('<');
		writer.write(elementName);
		writer.write('>');
		writeXMLEscapedText(text);
		writer.write("</");
		writer.write(elementName);
		writer.write('>');
	}

	/**
	 * Write XML element with elementName and escaped text.
	 * 
	 * @param elementName XML element name
	 * @param text input stream reader with element text
	 * @throws IOException
	 */
	public void writeSimpleXMLElement(String elementName, Reader text) throws IOException {
		writer.write('<');
		writer.write(elementName);
		writer.write('>');
		
		int ch = -1;
		while((ch = text.read()) != -1) {
		 writeXMLEscapedChar(ch);
		}
		
		writer.write("</");
		writer.write(elementName);
		writer.write('>');
	}
	
	
	/**
	 * Write XML element with elementName and escaped uuid.
	 * 
	 * @param elementName XML element name
	 * @param uuid element text  
	 * @throws IOException
	 */
	public void writeSimpleXMLElement(String elementName, UUID uuid) throws IOException {
		writeSimpleXMLElement(elementName, uuid != null ? uuid.toString() : null);
	}
	
	/**
	 * Write XML element with elementName and escaped uri.
	 * 
	 * @param elementName XML element name
	 * @param uri element text
	 * @throws IOException
	 */
	public void writeSimpleXMLElement(String elementName, URI uri) throws IOException {
		writeSimpleXMLElement(elementName, uri != null ? uri.toString() : null);
	}
	
	/**
	 * Write text escaped for XML.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	public void writeXMLEscapedText(String text) throws IOException {
		if (text == null) {
			return;
		}
		
		int length = text.length();
		for(int pos = 0; pos < length; pos += BLOCK_SIZE) {
			int end = pos +  BLOCK_SIZE;
			writeXMLEscapedTextBlock(text.substring(pos, end > length ? length : end));
		}
	}
	
	/**
	 * Write block of text escaped for XML.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	private void writeXMLEscapedTextBlock(String text) throws IOException {
		for(int i = 0; i < text.length(); i++){
		      char c = text.charAt(i);
		      writeXMLEscapedChar(c);
		   }
	}
	
	/**
	 * Write character escaped for XML.
	 * 
	 * @param ch char
	 * @throws IOException
	 */
	private void writeXMLEscapedChar(int ch) throws IOException {
		switch(ch){
		      case '<': writer.write("&lt;"); break;
		      case '>': writer.write("&gt;"); break;
		      case '\"': writer.write("&quot;"); break;
		      case '&': writer.write("&amp;"); break;
		      case '\'': writer.write("&apos;"); break;
		      default:
		          if(ch > 127 || ch < 32) {
		        	 writer.write("&#"+((int)ch)+";");
		         }else
		         writer.write(ch);
		}
	}
}