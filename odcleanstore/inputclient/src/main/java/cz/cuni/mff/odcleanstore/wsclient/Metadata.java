package cz.cuni.mff.odcleanstore.wsclient;

import java.util.ArrayList;
import java.util.List;

/**
 *  Metadata used in conjuction with payload in insert SOAP message of odcs-inputclient webservice.
 *  
 *  @author Petr Jerman
 */
public final class Metadata {

	private String _uuid;
	private List<String> _publishedBy;
	private List<String> _source;
	private List<String> _license;
	private String _dataBaseUrl;
	private String _provenanceBaseUrl;
	private String _provenance;
	private String _pipelineName;

	/**
	 * Get uuid of insert operation.
	 * @return uuid
	 */
	public String getUuid() {
		return _uuid;
	}

	/**
	 * Set uuid of insert operation.
	 * @param value uuid of insert operation
	 */
	public void setUuid(String value) {
		this._uuid = value;
	}

	/**
	 * Get appendable list of publishers of insert operation.
	 * @return list of publishers
	 */
	public List<String> getPublishedBy() {
		if (_publishedBy == null) {
			_publishedBy = new ArrayList<String>();
		}
		return this._publishedBy;
	}

	/**
	 * Get appendable list of sources of insert operation.
	 * @return appendable list of sources
	 */
	public List<String> getSource() {
		if (_source == null) {
			_source = new ArrayList<String>();
		}
		return this._source;
	}

	/**
	 * Get appendable list of licenses of insert operation.
	 * @return appendable list of licenses
	 */
	public List<String> getLicense() {
		if (_license == null) {
			_license = new ArrayList<String>();
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
	public String getDataBaseUrl() {
		return _dataBaseUrl;
	}

	/**
	 * Set base URL for payload.
	 * @param value base URL for payload
	 */
	public void setDataBaseUrl(String value) {
		this._dataBaseUrl = value;
	}

	/**
	 * Get base URL for provenance metadata.
	 * @return base URL for provenance metadata
	 */
	public String getProvenanceBaseUrl() {
		return _provenanceBaseUrl;
	}

	/**
	 * Set base URL for provenance metadata.
	 * @param value base URL for provenance metadata
	 */
	public void setProvenanceBaseUrl(String value) {
		this._provenanceBaseUrl = value;
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
