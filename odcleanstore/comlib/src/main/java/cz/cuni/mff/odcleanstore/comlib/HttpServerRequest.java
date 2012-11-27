package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import cz.cuni.mff.odcleanstore.comlib.io.InputStreamForHttp;

/**
 * Representation of request for http.
 * 
 * @author Petr Jerman
 */
public class HttpServerRequest {

	HttpServer server;

	InputStreamForHttp is;
	OutputStream os;

	String method;
	String requestPath;
	String contentType;
	Charset contentCharset;
	long contentLength;

	private Reader reader;
	private boolean disconnectedAtEndOfExecute;

	HttpServerRequest(HttpServer server, InputStreamForHttp is, OutputStream os) {
		this.server = server;
		this.os = os;
		this.is = is;
	}

	public String getMethod() {
		return method;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public String getContentType() {
		return contentType;
	}

	public Charset getContentCharset() {
		return contentCharset;
	}

	public long getContentLength() {
		return contentLength;
	}

	public synchronized Reader getReader() {
		if (reader == null) {
			reader = new InputStreamReader(is, contentCharset);
		}
	
		return reader;
	}

	public synchronized void sendSimpleResponse(int responseCode, String reasonPhrase) throws IOException {
		server.sendSimpleResponse(this, responseCode, reasonPhrase);
	}

	public synchronized void sendResponse(int responseCode, String contentType, String payload) throws IOException {
		server.sendResponse(this, responseCode, contentType, payload);
	}
	
	public boolean isDisconnectedAtEndOfExecute() {
		return disconnectedAtEndOfExecute;
	}

	public void setDisconnectedAtEndOfExecute(boolean disconnectedAtEndOfExecute) {
		this.disconnectedAtEndOfExecute = disconnectedAtEndOfExecute;
	}
}
