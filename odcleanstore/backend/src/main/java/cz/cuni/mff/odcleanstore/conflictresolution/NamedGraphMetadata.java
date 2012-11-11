package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Metadata for a single named graph in the RDF store.
 * All metadata except for namedGraphURI can be null.
 *
 * @author Jan Michelfeit
 */
public class NamedGraphMetadata {
    /** URI of the named graph for which this objects contains metadata. */
    private String namedGraphURI;

    /** URIs of data sources the named graph was retrieved from. */
    private Set<String> sources;

    /** URIs of publishers of data in the named graph. */
    private List<String> publishers;

    /** (Error localization) score of the named graph. */
    private Double score;

    /** (Error localization) scores of publishers (see {@link #publishers}). */
    private Double totalPublishersScore;

    /** Date the named graph was stored to the RDF store. */
    private Date insertedAt;

    /** The user that inserted the named graph. */
    private String insertedBy;

    /** Tag identifying a set of graphs that update each other. */
    private String updateTag;

    /** Licenses of the data. */
    private List<String> licenses;

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
     * Return data sources the named graph was retrieved from.
     * Returns null if the data source is unknown
     * @return list of URIs identifying sources of the named graph
     */
    public final Set<String> getSources() {
        return sources;
    }

    /**
     * Set data sources the named graph was retrieved from.
     * @param sources URIs of data source
     */
    public final void setSources(Set<String> sources) {
        this.sources = sources;
    }

    /**
     * Return URIs identifying publishers of data in the named graph.
     * Returns null if the publisher is unknown.
     * @return URIs of publishers or null
     */
    public final List<String> getPublishers() {
        return publishers;
    }

    /**
     * Set URIs identifying the publishers of data in the named graph in these metadata.
     * Null if the publishers is unknown.
     * @param publishers URIs of publishers
     */
    public final void setPublishers(List<String> publishers) {
        this.publishers = publishers;
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
     * Return (Quality Assessment) score of the publishers.
     * @return scores of publishers of the named graph or null if the score is unknown
     */
    public final Double getTotalPublishersScore() {
        return totalPublishersScore;
    }

    /**
     * Set (Quality Assessment) scores of the publishers.
     * @param totalPublishersScore total score of the publishers of the named graph or null
     *        if the score is unknown
     */
    public final void setTotalPublishersScore(Double totalPublishersScore) {
        this.totalPublishersScore = totalPublishersScore;
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
     * @return licenses of named graph data
     */
    public List<String> getLicences() {
        return licenses;
    }

    /**
     * @param licenses of named graph data
     */
    public void setLicences(List<String> licenses) {
        this.licenses = licenses;
    }


    /**
     * Returns update tag, which identifies set of graphs that update each other.
     * @return graph update tag
     */
    public final String getUpdateTag() {
        return updateTag;
    }

    /**
     * Sets update tag, which identifies set of graphs that update each other.
     * @param updateTag graph update tag
     */
    public final void setUpdateTag(String updateTag) {
        this.updateTag = updateTag;
    }

    @Override
    public String toString() {
        return "Metadata for " + namedGraphURI
                + " { Source: " + toStringWithNull(sources)
                + "; Publisher: " + toStringWithNull(publishers)
                + "; Score: " + toStringWithNull(score)
                + "; Publisher score: " + toStringWithNull(totalPublishersScore)
                + "; Inserted at: " + toStringWithNull(insertedAt)
                + "; Inserted by: " + toStringWithNull(insertedBy)
                + "; License: " + toStringWithNull(licenses)
                + " }";
    }

    /**
     * Null-proof conversion to string.
     * @param o object
     * @return string
     */
    private String toStringWithNull(Object o) {
        return o == null ? "null" : o.toString();
    }
}
