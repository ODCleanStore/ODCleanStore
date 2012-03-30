package cz.cuni.mff.odcleanstore.conflictresolution;

import com.hp.hpl.jena.graph.Triple;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Encapsulates settings for the conflict resolution process.
 *
 * @author Jan Michelfeit
 */
public class ConflictResolverSpec extends AggregationSpec {
    /** Prefix of URIs of named graphs where resolved triples are placed. */
    private String namedGraphURIPrefix;

    /** Set of URIs preferred as canonical URIs (used during implicit conflict resolution). */
    private Set<String> preferredURIs;

    /**
     * Collection of owl:sameAs links to be considered during the conflict
     * resolution proces.
     * If null, sameAsLinks are to be read from the data to resolve.
     */
    private Iterator<Triple> sameAsLinks;

    /**
     * Metadata about named graphs where the input triples for the conflict resolution
     * process come from. The metadata (e.g. quality score) will be considedered
     * during conflict resolution.
     * If null, metadata are to be read from the data to resolve.
     */
    private NamedGraphMetadataMap namedGraphMetadata;

    /**
     * Initialize default settings.
     */
    {
        sameAsLinks = null;
        namedGraphMetadata = null;
        preferredURIs = Collections.emptySet();
    }

    /**
     * Initialize this specification of conflict resolution settings with default
     * values.
     * @param namedGraphURIPrefix prefix of named graphs where resolved triples are placed
     */
    public ConflictResolverSpec(String namedGraphURIPrefix) {
        super();
        setNamedGraphURIPrefix(namedGraphURIPrefix);
    }

    /**
     * Return the prefix of named graphs where resolved triples are placed for
     * this specification.
     * @return the named graph URI prefix
     */
    public final String getNamedGraphPrefix() {
        return namedGraphURIPrefix;
    }

    /**
     * Set a prefix of named graphs where resolved triples are placed.
     * @param namedGraphURIPrefix the new named graph prefix
     */
    public final void setNamedGraphURIPrefix(String namedGraphURIPrefix) {
        if (namedGraphURIPrefix == null) {
            throw new IllegalArgumentException("Named graph prefix cannot be null");
        }
        this.namedGraphURIPrefix = namedGraphURIPrefix;
    }

    /**
     * Return set of URIs preferred as canonical URIs (used during implicit
     * conflict resolution) for this specification.
     * @return set of preferred URIs
     */
    public final Set<String> getPreferredURIs() {
        return preferredURIs;
    }

    /**
     * Set URIs preferred as canonical URIs (used during implicit conflict
     * resolution) for this specification.
     * @param preferredURIs the new set of preferred URIs
     */
    public final void setPreferredURIs(Set<String> preferredURIs) {
        if (preferredURIs == null) {
            throw new IllegalArgumentException("Set of preffered URIs cannot be null");
        }
        this.preferredURIs = preferredURIs;
    }

    /**
     * Return owl:sameAs links to be considered during the conflict resolution
     * proces for this specification.
     * @return collection of triples with owl:sameAs predicate or null
     */
    public final Iterator<Triple> getSameAsLinks() {
        return sameAsLinks;
    }

    /**
     * Set collection of owl:sameAs links to be considered during the conflict resolution
     * proces proces for this specification.
     * If set to null, sameAsLinks are to be read from the data to resolve.
     * @param sameAsLinks a collection of triples with owl:sameAs predicate or null
     */
    public final void setSameAsLinks(Iterator<Triple> sameAsLinks) {
        this.sameAsLinks = sameAsLinks;
    }

    /**
     * Return metadata about named graphs where the input triples for the conflict
     * resolution process come from. The metadata (e.g. quality score) will be
     * considedered during conflict resolution.
     * If the method returns null, metadata are to be read from the data to resolve.
     * @return map of metadata for named graphs or null
     */
    public final NamedGraphMetadataMap getNamedGraphMetadata() {
        return namedGraphMetadata;
    }

    /**
     * Set metadata about named graphs where the input triples for the conflict resolution
     * process come from for this specification.
     * If null, metadata are to be read from the data to resolve.
     * @param namedGraphMetadata the new map of metadata for named graphs or null
     */
    public final void setNamedGraphMetadata(NamedGraphMetadataMap namedGraphMetadata) {
        this.namedGraphMetadata = namedGraphMetadata;
    }
}
