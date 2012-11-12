package cz.cuni.mff.odcleanstore.comlib.soap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.UUID;

/**
 * SoapWriter is helper class for writing Soap Http request or response.
 * 
 * @author Petr Jerman
 */
public class SoapWriter {

private Writer writer;
	
	public SoapWriter(Writer writer) {
		this.writer =  new BufferedWriter(writer);
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
	
	public void closeQuietly() {
		try {
			writer.close();
		} catch (Exception e) {
			// do nothing
		}
	}
	
	/**
	 * Direct write block of text.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	public void write(String text) throws IOException {
		writer.write(text);
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
	public void writeSimpleXMLElement(String elementName, char[] chars, int count) throws IOException {
		writer.write('<');
		writer.write(elementName);
		writer.write('>');
		
		for(int i=0; i< count; i++) {
		 writeXMLEscapedChar(chars[i]);
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
	 * Write XML element with elementName and escaped uri.
	 * 
	 * @param elementName XML element name
	 * @param number element text
	 * @throws IOException
	 */
	public void writeSimpleXMLElement(String elementName, int number) throws IOException {
		writeSimpleXMLElement(elementName, Integer.toString(number));
	}

	/**
	 * Write block of text escaped for XML.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	private void writeXMLEscapedText(String text) throws IOException {
		if (text == null) {
			return;
		}
		
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
		         writer.write(ch);
		}
	}
}
