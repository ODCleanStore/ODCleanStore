package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * HttpWriter is helper class for writing Http request or response.
 * 
 * @author Petr Jerman
 */
public class HttpWriter extends Utf8WriterBase {
	
	private static final String CRLF = "\r\n";

	/**
     * Create object for writing Http request or response.
     * 
	 * @param stream outputstream for writing 
	 * @throws UnsupportedEncodingException 
     */
	public HttpWriter(OutputStream stream) throws UnsupportedEncodingException {
		super(stream);
	}
	
	/**
	 * Write CRLF to underline stream.
	 * 
	 * @throws IOException
	 */
	public void writeln() throws IOException {
		writer.write(CRLF);
	}
	
	/**
	 * Write text and CRLF to underline stream.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	public void writeln(String text) throws IOException {
		writer.write(text);
		writer.write(CRLF);
	}
}
