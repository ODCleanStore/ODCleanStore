package cz.cuni.mff.odcleanstore.wsclient.soap;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import cz.cuni.mff.odcleanstore.comlib.io.DummyOutputStream;
import cz.cuni.mff.odcleanstore.comlib.soap.SoapExecutor;
import cz.cuni.mff.odcleanstore.comlib.soap.SoapWriter;
import cz.cuni.mff.odcleanstore.comlib.soap.exceptions.SoapExecutorException;
import cz.cuni.mff.odcleanstore.wsclient.InsertException;
import cz.cuni.mff.odcleanstore.wsclient.Metadata;

/**
 * Implements Insert method of odcs-inputclient SOAP webservice.
 * 
 * @author Petr Jerman
 */
public final class InsertSoapMessage {

	private static final int RESPONSE_TIMEOUT = 60000;

	private static SoapExecutor soapExecutor = null;
	private static Object soapExecutorLock = new Object();

	HttpClientForInputWS httpClient;

	private String user;
	private String password;
	private Metadata metadata;
	private InsertException responseException;
	private Exception requestException;

	public static void send(URL serviceURL, String user, String password, Metadata metadata, Reader payloadReader,
			Reader payloadReaderForSize) throws InsertException {

		InsertSoapMessage insert = null;
		Thread responseThread = null;

		try {
			insert = new InsertSoapMessage(serviceURL, user, password, metadata, payloadReaderForSize);

			final InsertSoapMessage responseInsert = insert;
			responseThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						responseInsert.readResponse();
					} catch (InsertException e) {
						responseInsert.responseException = e;
					}
				}
			});
			responseThread.start();
		} catch (Exception e) {
			throw new InsertException(e);
		}

		try {
			insert.writeSoapPayload(insert.httpClient.getWriter(), payloadReader);
		} catch (IOException e) {
			insert.requestException = e;
		}

		try {
			responseThread.join(RESPONSE_TIMEOUT);
		} catch (InterruptedException e) {
			throw new InsertException(e);
		}

		if (responseThread.isAlive()) {
			insert.httpClient.closeQuietly();
			throw new InsertException(InsertException.CONNECTION_ERROR, "Connection error", "Response timeout");
		}

		if (insert.responseException != null) {
			throw insert.responseException;
		}

		if (insert.requestException != null) {
			throw new InsertException(insert.requestException);
		}
	}

	private InsertSoapMessage(URL serviceURL, String user, String password, Metadata metadata, Reader payloadReaderForSize)
			throws Exception {

		synchronized (soapExecutorLock) {
			if (soapExecutor == null) {
				soapExecutor = new SoapExecutor(InsertSoapMessage.class, "/inputws.xsd");
			}
		}

		this.user = user;
		this.password = password;
		this.metadata = metadata;

		DummyOutputStream dos = new DummyOutputStream();
		writeSoapPayload(new OutputStreamWriter(dos, "UTF-8"), payloadReaderForSize);
		dos.close();

		this.httpClient = new HttpClientForInputWS(serviceURL, dos.getCount());
	}

	/**
	 * Write SOAP message for odcs insert method.
	 * 
	 * @param writer otputstream writer
	 * @param payloadReader reader for read pauload
	 * 
	 * @throws IOException
	 */
	private void writeSoapPayload(Writer writer, Reader payloadReader) throws IOException {

		SoapWriter soapWriter = new SoapWriter(writer);
		soapWriter.write("<?xml version='1.0' encoding='UTF-8'?>"
				+ "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:i=\"http://inputws.engine.odcleanstore.mff.cuni.cz/\">" + "<s:Header/><s:Body><i:insert>");

		soapWriter.writeSimpleXMLElement("user", user);
		soapWriter.writeSimpleXMLElement("password", password);
		soapWriter.write("<metadata>");
		soapWriter.writeSimpleXMLElement("pipelineName", metadata.getPipelineName());
		soapWriter.writeSimpleXMLElement("uuid", metadata.getUUID());
		soapWriter.flush();

		for (URI uri : metadata.getPublishers()) {
			soapWriter.writeSimpleXMLElement("publishedBy", uri);
		}
		for (URI uri : metadata.getSources()) {
			soapWriter.writeSimpleXMLElement("source", uri);
		}
		for (URI uri : metadata.getLicenses()) {
			soapWriter.writeSimpleXMLElement("license", uri);
		}
		soapWriter.writeSimpleXMLElement("dataBaseUrl", metadata.getDataBaseUrl());
		soapWriter.writeSimpleXMLElement("provenance", metadata.getProvenance());
		soapWriter.writeSimpleXMLElement("updateTag", metadata.getUpdateTag());
		soapWriter.write("</metadata>");
		soapWriter.flush();

		int length = -1;
		char[] cbuf = new char[4096];
		while ((length = payloadReader.read(cbuf, 0, 4096)) != -1) {
			soapWriter.writeSimpleXMLElement("payload", cbuf, length);
		}

		soapWriter.write("</i:insert></s:Body></s:Envelope>");
		soapWriter.flush();
	}

	protected void readResponse() throws InsertException {
		Reader reader = null;
		try {
			try {
				reader = httpClient.getResponse();
			} catch (IOException e) {
				throw new InsertException(e);
			}

			if (httpClient.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				if (!httpClient.getResponseContentType().equalsIgnoreCase("text/xml")) {
					throw new InsertException(HttpURLConnection.HTTP_INTERNAL_ERROR);
				}

				InsertExceptionSoapHandler faultMessage = new InsertExceptionSoapHandler();
				try {
					soapExecutor.parse(reader, faultMessage);
				} catch (SoapExecutorException e) {
					throw new InsertException(e.getMessage());
				}

				if (faultMessage.getId() < 0) {
					throw new InsertException(faultMessage.getFaultString());
				} else {
					throw new InsertException(faultMessage.getId(), faultMessage.getMessage(), faultMessage.getMoreInfo());
				}
			}

			if (httpClient.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new InsertException(httpClient.getResponseCode());
			}
		} finally {
			httpClient.closeQuietly();
		}
	}
}
