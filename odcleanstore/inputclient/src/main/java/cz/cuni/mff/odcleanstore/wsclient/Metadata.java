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

	private UUID uuid;
	private List<URI> publishedBy;
	private List<URI> source;
	private List<URI> license;
	private URI dataBaseUrl;
	private String provenance;
	private String pipelineName;
	private String updateTag;
	
	/**
	 * Get uuid of insert operation.
	 * @return uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Set uuid of insert operation.
	 * @param value uuid of insert operation
	 */
	public void setUuid(UUID value) {
		this.uuid = value;
	}

	/**
	 * Get appendable list of publishers of insert operation.
	 * @return list of publishers
	 */
	public List<URI> getPublishedBy() {
		if (publishedBy == null) {
			publishedBy = new ArrayList<URI>();
		}
		return this.publishedBy;
	}

	/**
	 * Get appendable list of sources of insert operation.
	 * @return appendable list of sources
	 */
	public List<URI> getSource() {
		if (source == null) {
			source = new ArrayList<URI>();
		}
		return this.source;
	}

	/**
	 * Get appendable list of licenses of insert operation.
	 * @return appendable list of licenses
	 */
	public List<URI> getLicense() {
		if (license == null) {
			license = new ArrayList<URI>();
		}
		return this.license;
	}

	/**
	 * Get provenance metadata of insert operation.
	 * @return provenance metadata
	 */
	public String getProvenance() {
		return provenance;
	}

	/**
	 * Set provenance metadata of insert operation.
	 * @param value provenance metadata of insert operation 
	 */
	public void setProvenance(String value) {
		this.provenance = value;
	}

	/**
	 * Get base URL for payload.
	 * @return base URL for payload
	 */
	public URI getDataBaseUrl() {
		return dataBaseUrl;
	}

	/**
	 * Set base URL for payload.
	 * @param value base URL for payload
	 */
	public void setDataBaseUrl(URI value) {
		this.dataBaseUrl = value;
	}
	
	/**
	 * Get optional name for extra processing in odcs-cleanstore.
	 * @return name for extra processing in odcs-cleanstore
	 */
	public String getPipelineName() {
		return pipelineName;
	}

	/**
	 * Set optional name for extra processing in odcs-cleanstore.
	 * @param value name for extra processing in odcs-cleanstore
	 */
	public void setPipelineName(String value) {
		this.pipelineName = value;
	}
	
	/**
	 * Get optional name for identify update of same source.
	 * @return name for identify update of same source
	 */
	public String getUpdateTag() {
		return updateTag;
	}

	/**
	 * Set optional name for identify update of same source.
	 * @param value name for identify update of same source
	 */
	public void setUpdateTag(String value) {
		this.updateTag = value;
	}
}
