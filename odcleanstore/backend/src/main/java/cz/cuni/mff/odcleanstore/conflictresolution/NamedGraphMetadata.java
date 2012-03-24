package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Date;

/**
 * Metadata for a single named graph in the RDF store.
 * All metadata except for namedGraphURI can be null.
 *
 * @author Jan Michelfeit
 * @todo use Node_URI instead of String?
 */
public class NamedGraphMetadata {
    /** URI of the named graph for which this objects contains metadata. */
    private String namedGraphURI;

    /** URI of the data source the named graph was retrieved from. */
    private String dataSource;

    /** URI of the publisher of data in the named graph. */
    private String publisher;

    /** (Error localization) score of the named graph. */
    private Double score;

    /** (Error localization) score of the publisher (see {@link #publisher}). */
    private Double publisherScore;

    /** Date the named graph was stored to the RDF store. */
    private Date stored;

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
    public final String getDataSource() {
        return dataSource;
    }

    /**
     * Set data source the named graph was retrieved from.
     * @param dataSource URI of the data source
     */
    public final void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Return String identifying the publisher of data in the named graph.
     * Returns null if the publihser is unknown.
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
    public final Date getStored() {
        return stored;
    }

    /**
     * Set Date the named graph was stored to the RDF store in these metadata.
     * @param stored date the named graph was stored on or null if the date is unknown
     */
    public final void setStored(Date stored) {
        this.stored = stored;
    }
}
