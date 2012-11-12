package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;

import cz.cuni.mff.odcleanstore.comlib.io.DummyOutputStream;
import cz.cuni.mff.odcleanstore.comlib.io.HttpUtils;
import cz.cuni.mff.odcleanstore.comlib.io.InputStreamForHttp;

public abstract class HttpServer {

	private static final String RESPONSE_CHARSET = "UTF-8";

	private static final Pattern REQUEST_FIRST_LINE_PATTERN;
	private static final Pattern CONTENT_TYPE_PATTERN;
	private static final Pattern CONTENT_LENGTH_PATTERN;
	private static final Pattern CONTENT_ENCODING_PATTERN;

	static {
		REQUEST_FIRST_LINE_PATTERN = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+HTTP/1\\.[0-1].*$", Pattern.CASE_INSENSITIVE);

		CONTENT_TYPE_PATTERN = Pattern.compile("^\\s*Content-type\\s*:\\s*([^;]*);\\s*charset\\s*=\\s*\"?([^\"]*)\"?\\s*$",
				Pattern.CASE_INSENSITIVE);

		CONTENT_LENGTH_PATTERN = Pattern.compile("^\\s*Content-length\\s*:\\s*\"?(\\d*)\"?\\s*", Pattern.CASE_INSENSITIVE);

		CONTENT_ENCODING_PATTERN = Pattern.compile("^\\s*Content-encoding\\s*:\\s*\"?(\\S*)\"?\\s*", Pattern.CASE_INSENSITIVE);
	}

	private String hostName;
	private int port;
	private KeyManager[] keyManagers;

	private boolean isRunning;
	private boolean isAvailable;
	private ServerSocket serverSocket;
	private ScheduledThreadPoolExecutor executor;

	public HttpServer(String hostName, int port, KeyManager[] keyManagers) {
		this.hostName = hostName;
		this.port = port;
		this.keyManagers = keyManagers;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}

	public synchronized void start(ScheduledThreadPoolExecutor executor)
			throws IOException, NoSuchAlgorithmException, KeyManagementException {
		try {
			if (isRunning) {
				return;
			}

			this.executor = executor;

			if (keyManagers == null) {
				serverSocket = SocketFactory.createServerSocket(hostName, port);
			} else {
				serverSocket = SocketFactory.createSSLServerSocket(hostName, port, keyManagers);
			}

			executor.execute(new Runnable() {
				@Override
				public void run() {
					listenAndSendRequestToNewThread();
				}
			});

			// TODO pridat kontrolu zda-li server fakt bezi

			isRunning = true;
		} finally {
			if (!isRunning) {
				stop();
			}
		}
	}

	public synchronized void setAvailable(boolean isAvailable) {
		this.isAvailable = this.isRunning && isAvailable;
	}

	public synchronized void stop() throws IOException {
		if (!isRunning) {
			return;
		}

		isAvailable = false;
		isRunning = false;

		if (executor != null && !executor.isShutdown()) {
			executor.shutdownNow();
			executor = null;
		}
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
			serverSocket = null;
		}
	}

	private void listenAndSendRequestToNewThread() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (Exception e) {
				socket = null;
				ServerSocket ss = serverSocket;
				if (ss == null || ss.isClosed()) {
					return;
				}
				try {
					onListenForRequestException(e);
				} catch (Throwable dummy) {
				}
			}

			if (socket != null) {
				try {
					final Socket socketForExecution = socket;
					executor.execute(new Runnable() {
						@Override
						public void run() {
							mainSocketRequestHandler(socketForExecution);
						}
					});
				} catch (Exception e) {
					try {
						onCreateExecutionThreadForRequestException(e);
					} catch (Throwable dummy) {
					}
				}
			}
		}
	}

	private void mainSocketRequestHandler(Socket socket) {
		if (!isAvailable) {
			try {
				sendSimpleResponse(socket.getOutputStream(), HttpURLConnection.HTTP_UNAVAILABLE, "Service unavailable");
			} catch (Exception e) {
				try {
					onWriteServiceUnavailableResponseException(e);
				} catch (Throwable dummy) {
				}
			}
			return;
		}

		HttpServerRequest request = createRequestForSocket(socket);
		if (request != null) {
			try {
				executeRequest(request);
			} catch (Exception e) {
				try {
					onExecuteRequestException(e, request);
				} catch (Throwable dummy) {
				}
			}
		}

		if (request != null && request.isDisconnectedAtEndOfExecute()) {
			try {
				socket.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	private HttpServerRequest createRequestForSocket(Socket socket) {
		try {
			HttpServerRequest r = new HttpServerRequest(this, new InputStreamForHttp(socket.getInputStream()),
					socket.getOutputStream());

			String line = r.is.readAsciiLine();
			Matcher m = REQUEST_FIRST_LINE_PATTERN.matcher(line);
			if (m.matches()) {

				r.method = m.group(1);
				if (!r.method.equalsIgnoreCase("POST") && !r.method.equalsIgnoreCase("GET")
						&& !r.method.equalsIgnoreCase("HEADER")) {
					onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_NOT_IMPLEMENTED, "Not implemented");
					return null;
				}

				r.requestPath = URLDecoder.decode(m.group(2), "UTF-8");
				try {
					URI uri = new URI(r.requestPath);
					if (uri.getHost().equalsIgnoreCase(hostName)) {
						onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_NOT_FOUND, "Not found");
						return null;
					} else {
						r.requestPath = uri.getPath();
					}
				} catch (Exception e) {
					// do nothing
				}
			} else {
				onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_BAD_REQUEST, "Bad request");
				return null;
			}

			line = r.is.readAsciiLine();
			while (line != null && line.length() != 0) {

				m = CONTENT_TYPE_PATTERN.matcher(line);
				if (m.matches()) {
					r.contentType = m.group(1);

					String charset = m.group(2);
					if (charset != null && !charset.isEmpty()) {
						try {
							r.contentCharset = Charset.forName(charset);
						} catch (IllegalCharsetNameException e) {
							onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_BAD_REQUEST,
									"Bad content charset");
							return null;
						} catch (Exception e) {
							onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_INTERNAL_ERROR,
									"Not supported charset");
							return null;
						}
					}
				}

				m = CONTENT_LENGTH_PATTERN.matcher(line);
				if (m.matches()) {
					try {
						r.contentLength = Long.parseLong(m.group(1));
					} catch (Exception e) {
						onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_BAD_REQUEST,
								"Content length not recognised");
						return null;
					}
				}

				m = CONTENT_ENCODING_PATTERN.matcher(line);
				if (m.matches()) {
					if (!m.group(1).equalsIgnoreCase("identity")) {
						onCreateExecutionThreadForRequestException(r.os, HttpURLConnection.HTTP_UNSUPPORTED_TYPE,
								"Unsupported media type");
						return null;
					}
				}

				line = r.is.readAsciiLine();
			}

			if (r.contentCharset == null) {
				r.contentCharset = Charset.forName("ISO-8859-1");
			}
			return r;

		} catch (Exception e) {
			try {
				sendSimpleResponse(socket.getOutputStream(), HttpURLConnection.HTTP_INTERNAL_ERROR, null);
			} catch (Throwable dummy) {
			}
			try {
				onCreateRequestForSocketException(e);
			} catch (Throwable dummy) {
			}
			return null;
		}
	}

	private void onCreateExecutionThreadForRequestException(OutputStream os, int httpResponseCode, String message) {
		try {
			sendSimpleResponse(os, httpResponseCode, message);
		} catch (Throwable dummy) {
		}
		try {
			onCreateExecutionThreadForRequestException(httpResponseCode, message);
		} catch (Throwable dummy) {
		}
	}

	protected abstract void executeRequest(HttpServerRequest request);

	protected void onListenForRequestException(Exception e) {
	}

	protected void onCreateExecutionThreadForRequestException(int httpResponseCode, String message) {
	}

	protected void onCreateExecutionThreadForRequestException(Exception e) {
	}

	protected void onWriteServiceUnavailableResponseException(Exception e) {
	}

	protected void onCreateRequestForSocketException(Exception e) {
	}

	protected void onExecuteRequestException(Exception e, HttpServerRequest header) {
	}

	void sendSimpleResponse(HttpServerRequest request, int responseCode, String reasonPhrase) throws IOException {
		sendSimpleResponse(request.os, responseCode, reasonPhrase);
	}

	void sendResponse(HttpServerRequest request, int responseCode, String contentType, String payload) throws IOException {
		if (payload == null || payload.isEmpty()) {
			sendResponseHeader(request, responseCode, contentType, 0);
		} else {
			DummyOutputStream dos = new DummyOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(dos, RESPONSE_CHARSET);
			writer.write(payload);
			writer.close();

			HttpServer.sendResponseHeader(request, responseCode, contentType, dos.getCount());
			if (!request.method.equalsIgnoreCase("HEADER")) {
				writer = new OutputStreamWriter(request.os, RESPONSE_CHARSET);
				writer.write(payload);
				writer.flush();
			}
		}
	}

	private void sendSimpleResponse(OutputStream os, int responseCode, String reasonPhrase) throws IOException {
		HttpUtils.writeHeaderLine(os, String.format("HTTP/1.0 %d %s", responseCode, reasonPhrase == null ? "" : reasonPhrase));
		HttpUtils.writeHeaderLine(os, null);
		os.flush();
	}

	private static void sendResponseHeader(HttpServerRequest request, int responseCode, String contentType, long contentLength)
			throws IOException {
		HttpUtils.writeHeaderLine(request.os, "HTTP/1.0 %d", responseCode);
		HttpUtils.writeHeaderLine(request.os, "Content-Type: %s;charset=%s", contentType, RESPONSE_CHARSET);
		HttpUtils.writeHeaderLine(request.os, "Content-Length: %d", contentLength);
		HttpUtils.writeHeaderLine(request.os, "");
		request.os.flush();
	}
}
