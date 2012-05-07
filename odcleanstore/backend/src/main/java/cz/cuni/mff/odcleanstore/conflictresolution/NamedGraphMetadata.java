package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Date;

/**
 * Metadata for a single named graph in the RDF store.
 * All metadata except for namedGraphURI can be null.
 *
 * @author Jan Michelfeit
 */
public class NamedGraphMetadata {
    /** URI of the named graph for which this objects contains metadata. */
    private String namedGraphURI;

    /** URI of the data source the named graph was retrieved from. */
    private String source;

    /** URI of the publisher of data in the named graph. */
    private String publisher;

    /** (Error localization) score of the named graph. */
    private Double score;

    /** (Error localization) score of the publisher (see {@link #publisher}). */
    private Double publisherScore;

    /** Date the named graph was stored to the RDF store. */
    private Date insertedAt;

    /** The user that inserted the named graph. */
    private String insertedBy;

    /** The license of the data. */
    private String license;

    /**
     * Creates a new container of metadata for a named graph with the selected URI.
     * @param namedGraphURI URI of the named graph
     */
    public NamedGraphMetadata(String namedGraphURI) {
        this.namedGraphURI = namedGraphURI;
    }

    /**
     * Return URI of the named graph this metadata relate to.
     * @return URI of the named graph this metadata relate to.
     */
    public final String getNamedGraphURI() {
        return namedGraphURI;
    }

    /**
     * Return data source the named graph was retrieved from.
     * Returns null if the data source is unknown
     * @return URI identifying the source of the named graph
     * @todo return set/collection of String
     */
    public final String getSource() {
        return source;
    }

    /**
     * Set data source the named graph was retrieved from.
     * @param source URI of the data source
     */
    public final void setSource(String source) {
        this.source = source;
    }

    /**
     * Return String identifying the publisher of data in the named graph.
     * Returns null if the publisher is unknown.
     * @return URI of the publisher or null
     */
    public final String getPublisher() {
        return publisher;
    }

    /**
     * Set URI identifying the publisher of data in the named graph in these metadata.
     * Null if the publisher is unknown.
     * @param publisher URI of the publisher
     */
    public final void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Return (error localization) score of the named graph.
     * @return score of the named graph or null if the score is unknown
     */
    public final Double getScore() {
        return score;
    }

    /**
     * Set (error localization) score of the named graph in these metadata.
     * @param score of the named graph or null if the score is unknown
     */
    public final void setScore(Double score) {
        this.score = score;
    }

    /**
     * Return (error localization) score of the publisher.
     * @return score of the publisher of the named graph or null if the score is unknown
     */
    public final Double getPublisherScore() {
        return publisherScore;
    }

    /**
     * Set (error localization) score of the publisher.
     * @param publisherScore score of the publisher of the named graph or null
     *        if the score is unknown
     */
    public final void setPublisherScore(Double publisherScore) {
        this.publisherScore = publisherScore;
    }

    /**
     * Return date the named graph was stored to the RDF store.
     * @return date the named graph was stored on or null if the date is unknown
     */
    public final Date getInsertedAt() {
        return insertedAt;
    }

    /**
     * Set Date the named graph was stored to the RDF store in these metadata.
     * @param stored date the named graph was stored on or null if the date is unknown
     */
    public final void setInsertedAt(Date stored) {
        this.insertedAt = stored;
    }

    /**
     * @return the user that inserted the graph
     */
    public String getInsertedBy() {
        return insertedBy;
    }

    /**
     * @param insertedBy the user that inserted the graph
     */
    public void setInsertedBy(String insertedBy) {
        this.insertedBy = insertedBy;
    }

    /**
     * @return license of named graph data
     */
    public String getLicence() {
        return license;
    }

    /**
     * @param license of named graph data
     */
    public void setLicence(String license) {
        this.license = license;
    }
}
