package cz.cuni.mff.odcleanstore.wsclient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  Metadata used in conjuction with payload in insert SOAP message of odcs-inputclient webservice.
 *  
 *  @author Petr Jerman
 */
public final class Metadata {

	private UUID _uuid;
	private List<URI> _publishedBy;
	private List<URI> _source;
	private List<URI> _license;
	private URI _dataBaseUrl;
	private String _provenance;
	private String _pipelineName;

	/**
	 * Get uuid of insert operation.
	 * @return uuid
	 */
	public UUID getUuid() {
		return _uuid;
	}

	/**
	 * Set uuid of insert operation.
	 * @param value uuid of insert operation
	 */
	public void setUuid(UUID value) {
		this._uuid = value;
	}

	/**
	 * Get appendable list of publishers of insert operation.
	 * @return list of publishers
	 */
	public List<URI> getPublishedBy() {
		if (_publishedBy == null) {
			_publishedBy = new ArrayList<URI>();
		}
		return this._publishedBy;
	}

	/**
	 * Get appendable list of sources of insert operation.
	 * @return appendable list of sources
	 */
	public List<URI> getSource() {
		if (_source == null) {
			_source = new ArrayList<URI>();
		}
		return this._source;
	}

	/**
	 * Get appendable list of licenses of insert operation.
	 * @return appendable list of licenses
	 */
	public List<URI> getLicense() {
		if (_license == null) {
			_license = new ArrayList<URI>();
		}
		return this._license;
	}

	/**
	 * Get provenance metadata of insert operation.
	 * @return provenance metadata
	 */
	public String getProvenance() {
		return _provenance;
	}

	/**
	 * Set provenance metadata of insert operation.
	 * @param value provenance metadata of insert operation 
	 */
	public void setProvenance(String value) {
		this._provenance = value;
	}

	/**
	 * Get base URL for payload.
	 * @return base URL for payload
	 */
	public URI getDataBaseUrl() {
		return _dataBaseUrl;
	}

	/**
	 * Set base URL for payload.
	 * @param value base URL for payload
	 */
	public void setDataBaseUrl(URI value) {
		this._dataBaseUrl = value;
	}
	
	/**
	 * Get optional name for extra processing in odcs-cleanstore.
	 * @return name for extra processing in odcs-cleanstore
	 */
	public String getPipelineName() {
		return _pipelineName;
	}

	/**
	 * Set optional name for extra processing in odcs-cleanstore.
	 * @param value name for extra processing in odcs-cleanstore
	 */
	public void setPipelineName(String value) {
		this._pipelineName = value;
	}
}
