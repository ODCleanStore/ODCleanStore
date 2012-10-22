package cz.cuni.mff.odcleanstore.wsclient;

import java.io.Reader;
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
		insert.Run(user, password, metadata, payload);
	}
	
	/**
	 * Send insert data message to odcs-inputclient SOAP webservice.
	 * 
	 * @param user odcs user for message
	 * @param password odcs user password for message
	 * @param metadata metadata asocciated with payload
	 * @param payloadReader Reader with payload in rdfxml or ttl format
	 * @param payloadReaderForSize Reader for computing payload size
	 *  
	 * @throws InsertException Exception returned from server or client
	 */
	public void insert(String user, String password, Metadata metadata, Reader payloadReader, Reader payloadReaderForSize) throws InsertException {
		Insert insert = new Insert(serviceURL);
		insert.Run(user, password, metadata, payloadReader, payloadReaderForSize);
	}
}

