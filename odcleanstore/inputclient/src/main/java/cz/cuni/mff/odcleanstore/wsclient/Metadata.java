package cz.cuni.mff.odcleanstore.wsclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Metadata associated with data inserted to ODCleanStore through the Input Webservice.
 * While most metadata items are optional, several values are required:
 * <ul>
 *  <li>UUID of the insert operation</li>
 *  <li>At least one publisher</li>
 *  <li>At least one source</li>
 * </ul>
 * 
 * @author Petr Jerman
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

    public Metadata(UUID uuid) {
        this.uuid = uuid;
        try {
            this.dataBaseUrl = new URI("");
        } catch (URISyntaxException e) {
            // do nothing
        }
    }
    
    /**
     * Get UUID of the insert operation.
     * Every insertion of data must have a unique UUID; this value is required.
     * @return UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Set UUID of the insert operation.
     * Every insertion of data must have a unique UUID; this value is required.
     * @param value UUID of insert operation
     */
    public void setUUID(UUID value) {
        this.uuid = value;
    }

    /**
     * Returns appendable list of publishers (identified by a URI) of the data.
     * At least one publisher is required.
     * @return list of publishers
     */
    public List<URI> getPublishers() {
        if (publishedBy == null) {
            publishedBy = new ArrayList<URI>();
        }
        return this.publishedBy;
    }
    
    /**
     * Adds a publisher the list of publishers.
     * At least one source is required.
     * @see #getPublishers()
     */
    public void addPublisher(URI publisher) {
        getPublishers().add(publisher);
    }

    /**
     * Returns appendable list of sources of inserted data (as URIs).
     * At least one source is required.
     * @return appendable list of sources
     */
    public List<URI> getSources() {
        if (source == null) {
            source = new ArrayList<URI>();
        }
        return this.source;
    }

    /**
     * Adds a source the list of sources.
     * At least one source is required.
     * @see #getSources()
     */
    public void addSource(URI sourceURI) {
        getSources().add(sourceURI);
    }
    
    /**
     * Get appendable list of licenses of insert operation. Each license is identified by a URI.
     * Licenses are optional.
     * @return appendable list of licenses
     */
    public List<URI> getLicenses() {
        if (license == null) {
            license = new ArrayList<URI>();
        }
        return this.license;
    }
    
    /**
     * Adds a license the list of licenses.
     * @see #getLicenses()
     */
    public void addLicense(URI licenseURI) {
        getLicenses().add(licenseURI);
    }

    /**
     * Get additional provenance metadata associated with the inserted data.
     * The provenance metadata are RDF data serialized as RDF/XML or Turtle.
     * This value is optional.
     * @return provenance metadata
     */
    public String getProvenance() {
        return provenance;
    }

    /**
     * Set additional provenance metadata associated with the inserted data.
     * The provenance metadata are RDF data serialized as RDF/XML or Turtle.
     * This value is optional.
     * @param value provenance metadata 
     */
    public void setProvenance(String value) {
        this.provenance = value;
    }

    /**
     * Get base URL for inserted RDF data.
     * @return base URL for inserted RDF data
     */
    public URI getDataBaseUrl() {
        return dataBaseUrl;
    }

    /**
     * Set base URL for inserted RDF data.
     * @param value base URL for inserted RDF data
     */
    public void setDataBaseUrl(URI value) {
        this.dataBaseUrl = value;
    }

    /**
     * Returns name of data-processing pipeline to be used to process the inserted data in ODCleanStore.
     * This value is optional.
     * @return name of data-processing pipeline
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Sets name of data-processing pipeline to be used to process the inserted data in ODCleanStore.
     * This value is optional.
     * @param pipelineName name of data-processing pipeline
     */
    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    /**
     * Returns value of update tag which identifies a set of graphs that update each other.
     * @see #setUpdateTag(String)
     * @return value of update tag
     */
    public String getUpdateTag() {
        return updateTag;
    }

    /**
     * Sets value of update tag which identifies a set of graphs that update each other.
     * An inserted named graph A is considered an update of named graph B if 
     * <ul>
     *  <li>Graphs A and B have the same update tag, or both have a null update tag.</li>
     *  <li>Graphs A and B were inserted by the same user.</li>
     *  <li>Graphs A and B have the same set of sources in metadata (see {@link #getSources()}).</li>
     * </ul>
     * This value is optional.
     * @return value of update tag
     */
    public void setUpdateTag(String value) {
        this.updateTag = value;
    }
}
