package cz.cuni.mff.odcleanstore.wsclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *  Odcs-inputclient SOAP webservice java client.
 *  
 *  @author Petr Jerman
 */
public final class OdcsService {

	private URL serviceURL;

	/**
	 * Create new odcs-inputclient webservice java client.
	 * 
	 * @param serviceLocation odcs-inputclient webservice location
	 * @throws MalformedURLException serviceLocation URL format error
	 */
	public OdcsService(String serviceLocation) throws MalformedURLException {
		try {
			this.serviceURL = new URI(serviceLocation).toURL();
		} catch (IllegalArgumentException e) {
			throw new MalformedURLException();
		} catch (NullPointerException e) {
			throw new MalformedURLException();
		} catch (URISyntaxException e) {
			throw new MalformedURLException(); 
		}
	}

	/**
	 * Send insert data message to odcs-inputclient SOAP webservice.
	 * 
	 * @param user odcs user for message
	 * @param password odcs user password for message
	 * @param metadata metadata asocciated with payload
	 * @param payload payload in rdfxml or ttl format
	 * @throws InsertException Exception returned from server or client
	 */
	public void insert(String user, String password, Metadata metadata, String payload) throws InsertException {
		Insert insert = new Insert(serviceURL);
		insert.Run(user, password, metadata, new StringReader(payload), new StringReader(payload));
	}
	
	/**
	 * Send insert data message to odcs-inputclient SOAP webservice.
	 * 
	 * @param user odcs user for message
	 * @param password odcs user password for message
	 * @param metadata metadata asocciated with payload
	 * @param payloadFileName name of file with payload in rdfxml or ttl format
	 * @param payloadFileEncodingName encoding of file with payload ... UTF-8 and so on
	 * @throws InsertException Exception returned from server or client
	 * @throws FileNotFoundException  payload file not found
	 * @throws UnsupportedEncodingException payload file encoding not supported
	 */
	public void insert(String user, String password, Metadata metadata, File payloadFileName, String payloadFileEncodingName) throws InsertException, FileNotFoundException, UnsupportedEncodingException  {
		Insert insert = new Insert(serviceURL);
		Reader payloadReader = new InputStreamReader(new FileInputStream(payloadFileName), payloadFileEncodingName);
		Reader payloadReaderForSize = new InputStreamReader(new FileInputStream(payloadFileName), payloadFileEncodingName);
		insert.Run(user, password, metadata, payloadReader, payloadReaderForSize);
	}
}

