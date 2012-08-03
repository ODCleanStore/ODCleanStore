package cz.cuni.mff.odcleanstore.wsclient;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.cuni.mff.odcleanstore.engine.inputws.InsertException_Exception;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWS;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWSService;

/**
 *  Odcs-inputclient SOAP webservice java client wrapper.
 *  Java 1.6.4 or higher required.
 *  
 *  @author Petr Jerman
 */
public final class OdcsService {

	private InputWSService _inputWSService;
	private InputWS _inputWSPort;

	/**
	 * Create new odcs-inputclient webservice java client wrapper.
	 * @param serviceLocation odcs-inputclient webservice location
	 * @throws MalformedURLException serviceLocation URL format error
	 */
	public OdcsService(String serviceLocation) throws MalformedURLException {
		_inputWSService = InputWSService.create(serviceLocation);
		_inputWSPort = _inputWSService.getInputWSPort();
	}

	/**
	 * Send insert data message to odcs-inputclient SOAP webservice.
	 * 
	 * @param user odcs user for message
	 * @param password odcs user password for message
	 * @param metadata metadata asocciated with payload
	 * @param payload payload in rdfxml or ttl format
	 * @throws InsertException Exception returned from server
	 */
	public void insert(String user, String password, Metadata metadata, String payload) throws InsertException {

		cz.cuni.mff.odcleanstore.engine.inputws.Metadata wsMetadata = new cz.cuni.mff.odcleanstore.engine.inputws.Metadata();
	
		wsMetadata.setUuid(convert(metadata.getUuid()));
		wsMetadata.getPublishedBy().addAll(convert(metadata.getPublishedBy()));
		wsMetadata.getSource().addAll(convert(metadata.getSource()));
		wsMetadata.getLicense().addAll(convert(metadata.getLicense()));
		wsMetadata.setDataBaseUrl(convert(metadata.getDataBaseUrl()));
		wsMetadata.setProvenanceBaseUrl(convert(metadata.getProvenanceBaseUrl()));
		wsMetadata.setProvenance(metadata.getProvenance());
		wsMetadata.setPipelineName(metadata.getPipelineName());

		try {
			_inputWSPort.insert(user, password, wsMetadata, payload);
		} catch (InsertException_Exception e) {
			throw new InsertException(e.getFaultInfo().getId(), e.getFaultInfo().getMessage(), e.getFaultInfo().getMoreInfo());
		}
	}
	
	private List<String> convert(List<URI> src) {
		List<String> retVal= new ArrayList<String>();
		for(URI item:src) {
			if(item != null) {
				retVal.add(item.toString());
			}
		}
		return retVal;
	}
	
	private String convert(URI src) {
		return src != null ? src.toString() : null;
	}

	private String convert(UUID src) {
		return src != null ? src.toString() : null;
	}
}
