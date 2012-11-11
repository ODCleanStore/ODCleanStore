package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.KeyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.comlib.ComlibUtils;
import cz.cuni.mff.odcleanstore.comlib.HttpServer;
import cz.cuni.mff.odcleanstore.comlib.HttpServerRequest;
import cz.cuni.mff.odcleanstore.comlib.soap.SoapExecutor;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapExecutorException;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;

public class InputWSHttpServer extends HttpServer {

	private static final Logger LOG = LoggerFactory.getLogger(InputWSHttpServer.class);

	private String location;
	private SoapExecutor soapExecutor = null;

	public InputWSHttpServer(URL serverURL, KeyManager[] keyManager) throws SAXException {
		super(serverURL.getHost(), serverURL.getPort(), keyManager);
		location = serverURL.toString();
		soapExecutor = new SoapExecutor(Engine.class, "/inputws.xsd");
	}

	@Override
	protected void executeRequest(HttpServerRequest request) {
		try {
			if (request.getRequestPath().equalsIgnoreCase("/inputws") && request.getMethod().equalsIgnoreCase("POST")) {

				executeSoapMessage(request);

			} else if (request.getRequestPath().equalsIgnoreCase("/inputws?wsdl")) {

				String wsdl = ComlibUtils.loadResourceToString(getClass(), "/inputws.wsdl", null);
				wsdl = wsdl.replaceAll("##-location-##", location);
				request.sendResponse(HttpURLConnection.HTTP_OK, "text/xml", wsdl);

			} else if (request.getRequestPath().equalsIgnoreCase("/inputws?xsd=1")) {

				request.sendResponse(HttpURLConnection.HTTP_OK, "text/xml",
						ComlibUtils.loadResourceToString(getClass(), "/inputws.xsd", null));

			} else {

				request.sendSimpleResponse(HttpURLConnection.HTTP_NOT_FOUND, "Not found");
				request.setDisconnectedAtEndOfExecute(true);
			}
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void executeSoapMessage(HttpServerRequest request) throws IOException {
		InsertExecutor insertExecutor = null;
		boolean isSuccess = false;
		try {
			insertExecutor = new InsertExecutor();
			soapExecutor.parse(request.getReader(), insertExecutor);
			isSuccess = true;
			request.sendSimpleResponse(HttpURLConnection.HTTP_OK, null);
		} catch (SoapExecutorException e) {
			if (e.getCause() == null) {
				LOG.warn("InputWS - {}", e.getMessage());
			} else {
				LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
			}
			request.sendResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "text/xml", e.getSoapMessage());
			return;
		} catch (Exception e) {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
			request.sendSimpleResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
			request.setDisconnectedAtEndOfExecute(true);
			return;
		} finally {
			if (!isSuccess && insertExecutor != null) {
				insertExecutor.cleanOnError();
			}
		}
	}

	protected void onListenForRequestException(Exception e) {
		LOG.warn("InputWS - {}", e.getMessage());
	}

	protected void onCreateExecutionThreadForRequestException(int httpResponseCode, String message) {
		if (message == null || message.isEmpty()) {
			LOG.warn("InputWS - Send {} http response code to client", httpResponseCode);
		} else {
			LOG.warn("InputWS - Send {} http response code to client with reason phrase {}", httpResponseCode, message);
		}
	}

	protected void onCreateExecutionThreadForRequestException(Exception e) {
		if (e.getCause() == null) {
			LOG.warn("InputWS - {}", e.getMessage());
		} else {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
		}
	}

	protected void onWriteServiceUnavailableResponseException(Exception e) {
		if (e.getCause() == null) {
			LOG.warn("InputWS - {}", e.getMessage());
		} else {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
		}
	}

	protected void onCreateRequestForSocketException(Exception e) {
		if (e.getCause() == null) {
			LOG.warn("InputWS - {}", e.getMessage());
		} else {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
		}
	}

	protected void onExecuteRequestException(Exception e, HttpServerRequest header) {
		if (e.getCause() == null) {
			LOG.warn("InputWS - {}", e.getMessage());
		} else {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
		}
	}

	protected void onShutdownSocketException(Exception e) {
		if (e.getCause() == null) {
			LOG.warn("InputWS - {}", e.getMessage());
		} else {
			LOG.warn(FormatHelper.formatExceptionForLog(e, "InputWS - %s", e.getMessage()));
		}
	}
}
