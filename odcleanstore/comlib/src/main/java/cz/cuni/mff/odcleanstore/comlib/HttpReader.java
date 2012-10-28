package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpReader is helper class for reading Http request or response.
 * 
 * @author Petr Jerman
 */
public class HttpReader extends Utf8ReaderBase {

	private static final Pattern RESPONSE_FIRST_LINE_PATTERN;
	private static final Pattern CONTENT_TYPE_PATTERN;
	
	static {
		RESPONSE_FIRST_LINE_PATTERN = Pattern.compile("^HTTP/1\\.[0-1]\\s*([0-9]{3}).*$", Pattern.CASE_INSENSITIVE);
		CONTENT_TYPE_PATTERN = Pattern.compile("^\\s*Content-type\\s*:\\s*([^;]*);\\s*charset\\s*=\\s*\"*([^\"]*)\"?\\s*$", Pattern.CASE_INSENSITIVE); 
	}
	
	private int responseCode;
	private String contentType;
	private String charset;
	
	/**
	 * @return HTTP response code, valid after readResponseHttpHeader method is called
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @return HTTP ContentType, valid after readResponseHttpHeader method is called
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return HTTP ContentType character set, valid after readResponseHttpHeader method is called
	 */
	public String getCharset() {
		return charset;
	}
	
	/**
     * Create object for reading Http request or response.
     * 
     * @param stream inputstream with http request or response.
	 * @throws UnsupportedEncodingException 
     */
	public HttpReader(InputStream stream) throws UnsupportedEncodingException {
		super(stream);
	}
	
	/**
	 * Read response http header.
	 * 
	 * @throws IOException
	 */
	public void readResponseHttpHeader() throws UnsupportedEncodingException, IOException {
	
		String line = reader.readLine();
		if (line == null) throw new IOException();
		
		Matcher m = RESPONSE_FIRST_LINE_PATTERN.matcher(line);
		if(m.matches()){
			try {
				responseCode = Integer.parseInt(m.group(1));
			} catch(Exception e) {
				throw new IOException();
			}
		}
		else throw new IOException();
		
		line = reader.readLine();
		
		while (line != null && line.length() != 0) {
			m = CONTENT_TYPE_PATTERN.matcher(line);
			if(m.matches()){
				contentType = m.group(1);
				charset = m.group(2);
			}
			line = reader.readLine();
		}
		
		if(charset == null || !charset.equalsIgnoreCase("utf-8")) {
			throw new UnsupportedEncodingException();
		}
	}
}
