package cz.cuni.mff.odcleanstore.wsclient.soap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.odcleanstore.comlib.SocketFactory;
import cz.cuni.mff.odcleanstore.comlib.io.HttpUtils;
import cz.cuni.mff.odcleanstore.comlib.io.InputStreamForHttp;

/**
 * Implements simple http lcient for insert method of odcs-inputclient SOAP webservice.
 * 
 * @author Petr Jerman
 */

public class HttpClientForInputWS {

	private static final Pattern RESPONSE_FIRST_LINE_PATTERN;
	private static final Pattern CONTENT_TYPE_PATTERN;

	static {
		RESPONSE_FIRST_LINE_PATTERN = Pattern.compile("^HTTP/1\\.[0-1]\\s*([0-9]{3}).*$", Pattern.CASE_INSENSITIVE);
		CONTENT_TYPE_PATTERN = Pattern.compile("^\\s*Content-type\\s*:\\s*([^;]*);\\s*charset\\s*=\\s*\"?([^\"]*)\"?\\s*$",
				Pattern.CASE_INSENSITIVE);
	}

	private URL serverURL;
	private Socket socket;
	private Writer writer;

	private long contentLength;
	private int responseCode = -1;
	private String responseContentType;
	private String responseContentCharset;

	/**
	 * Create instance of simple http lcient for insert method of odcs-inputclient SOAP webservice.
	 * 
	 * @param serverURL
	 * @param contentLength
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public HttpClientForInputWS(URL serverURL, long contentLength) throws UnknownHostException, IOException,
			KeyManagementException, NoSuchAlgorithmException {

		this.serverURL = serverURL;
		this.contentLength = contentLength;

		if (serverURL.getProtocol().equalsIgnoreCase("https")) {
			socket = SocketFactory.createAllTrustSSLSocket(serverURL.getHost(), serverURL.getPort());
		} else {
			socket = SocketFactory.createClientSocket(serverURL.getHost(), serverURL.getPort());
		}
	}

	/**
	 * Write http reader and prepare writer of http content.
	 * 
	 * @return writer for writing http content
	 * @throws IOException
	 */
	public Writer getWriter() throws IOException {
		if (writer == null) {
			OutputStream os = socket.getOutputStream();
			sendRequestHeader(os);
			writer = new OutputStreamWriter(os, "UTF-8");
		}
		return writer;
	}

	/**
	 * Read http header and prepare reader for reading http content.
	 * 
	 * @return reader for reading http content
	 * @throws IOException
	 */
	public Reader getResponse() throws IOException {

		InputStreamForHttp is = new InputStreamForHttp(socket.getInputStream());

		String line = is.readAsciiLine();
		if (line == null)
			throw new IOException("Http header is empty");

		Matcher m = RESPONSE_FIRST_LINE_PATTERN.matcher(line);
		if (m.matches()) {
			try {
				responseCode = Integer.parseInt(m.group(1));
			} catch (Exception e) {
				throw new IOException("Not recognised http response code");
			}
		} else
			throw new IOException("Not recognised http response");

		line = is.readAsciiLine();

		while (line != null && line.length() != 0) {
			m = CONTENT_TYPE_PATTERN.matcher(line);
			if (m.matches()) {
				responseContentType = m.group(1);
				responseContentCharset = m.group(2);
			}
			line = is.readAsciiLine();
		}

		if (responseContentCharset == null) {
			responseContentCharset = "ISO-8859-1";
		}

		return new InputStreamReader(is, responseContentCharset);
	}

	/**
	 * @return http response code
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @return http response type
	 */
	public String getResponseContentType() {
		return responseContentType == null ? "" : responseContentType;
	}

	/**
	 * @return http response character set
	 */
	public String getResponseContentCharSet() {
		return responseContentCharset == null ? "" : responseContentCharset;
	}

	/**
	 * Send http request header to the server.
	 * 
	 * @param os output stream for writing header
	 * @throws IOException
	 */
	private void sendRequestHeader(OutputStream os) throws IOException {
		HttpUtils.writeHeaderLine(os, "POST %s HTTP/1.0", URLEncoder.encode(serverURL.getPath(), "UTF-8"));
		HttpUtils.writeHeaderLine(os, "Content-Type:text/xml;charset=UTF-8");
		HttpUtils.writeHeaderLine(os, "Content-Length:%d", contentLength);
		HttpUtils.writeHeaderLine(os, "Accept:text/xml");
		HttpUtils.writeHeaderLine(os, "Accept-Charset:UTF-8");
		HttpUtils.writeHeaderLine(os, "SOAPAction: \"\"");
		HttpUtils.writeHeaderLine(os, null);
		os.flush();
	}

	/**
	 * Close socket without any exceptions.
	 */
	public void closeQuietly() {

		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}
