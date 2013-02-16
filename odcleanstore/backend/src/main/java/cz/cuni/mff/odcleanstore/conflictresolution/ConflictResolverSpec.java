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
public class ConflictResolverSpec {
    /** Prefix of URIs of named graphs where resolved triples are placed. */
    private String namedGraphURIPrefix;

    /** Set of URIs preferred as canonical URIs (used during implicit conflict resolution). */
    private Set<String> preferredURIs;

    /**
     * Collection of owl:sameAs links to be considered during the conflict resolution process.
     * If null, sameAsLinks are to be read from the data to resolve.
     */
    private Iterator<Triple> sameAsLinks;

    /**
     * Metadata about named graphs where the input triples for the conflict resolution
     * process come from. The metadata (e.g. quality score) will be considered
     * during conflict resolution.
     * If null, metadata are to be read from the data to resolve.
     */
    private NamedGraphMetadataMap namedGraphMetadata;

    /**
     * Aggregation settings. These settings have priority over {@link #defaultAggregationSpec}.
     * @see #defaultAggregationSpec
     * @param aggregationSpec aggregation settings to use
     */
    private AggregationSpec aggregationSpec;

    /**
     * Default aggregation settings.
     * @see #aggregationSpec
     * @param aggregationSpec aggregation settings to use
     */
    private AggregationSpec defaultAggregationSpec;

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
        this(namedGraphURIPrefix, new AggregationSpec(), new AggregationSpec());
    }

    /**
     * Initialize this specification of conflict resolution settings with the given aggregation
     * settings.
     * Two kinds of AggregationSpec are required because two instances cannot be merged without
     * translation of URIs to the same owl:sameAs equivalent, which is done only in Conflict Resolution.
     * @param namedGraphURIPrefix prefix of named graphs where resolved triples are placed
     * @param aggregationSpec aggregation settings to use; overrides defaultAggregationSpec
     * @param defaultAggregationSpec aggregation settings; overridden by aggregationSpec
     */
    public ConflictResolverSpec(String namedGraphURIPrefix, AggregationSpec aggregationSpec,
            AggregationSpec defaultAggregationSpec) {
        setNamedGraphURIPrefix(namedGraphURIPrefix);
        this.aggregationSpec = aggregationSpec;
        this.defaultAggregationSpec = defaultAggregationSpec;
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
     * Return owl:sameAs links to be considered during the conflict resolution process for this specification.
     * @return collection of triples with owl:sameAs predicate or null
     */
    public final Iterator<Triple> getSameAsLinks() {
        return sameAsLinks;
    }

    /**
     * Set collection of owl:sameAs links to be considered during the conflict resolution
     * process for this specification.
     * If set to null, sameAsLinks are to be read from the data to resolve.
     * @param sameAsLinks a collection of triples with owl:sameAs predicate or null
     */
    public final void setSameAsLinks(Iterator<Triple> sameAsLinks) {
        this.sameAsLinks = sameAsLinks;
    }

    /**
     * Return metadata about named graphs where the input triples for the conflict
     * resolution process come from. The metadata (e.g. quality score) will be
     * considered during conflict resolution.
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

    /**
     * Returns aggregation settings.
     * These settings have priority over those set by {@link #setDefaultAggregationSpec(AggregationSpec)}.
     * @return aggregation settings
     */
    public final AggregationSpec getAggregationSpec() {
        return aggregationSpec;
    }

    /**
     * Sets aggregation settings.
     * These settings have priority over those set by {@link #setDefaultAggregationSpec(AggregationSpec)}.
     * @param aggregationSpec aggregation settings
     */
    public final void setAggregationSpec(AggregationSpec aggregationSpec) {
        this.aggregationSpec = aggregationSpec;
    }

    /**
     * Returns default aggregation settings.
     * @see #getAggregationSpec()
     * @return the aggregationSpec aggregation settings
     */
    public final AggregationSpec getDefaultAggregationSpec() {
        return defaultAggregationSpec;
    }

    /**
     * Returns default aggregation settings.
     * @see #setAggregationSpec()
     * @param defaultAggregationSpec aggregation settings
     */
    public final void setDefaultAggregationSpec(AggregationSpec defaultAggregationSpec) {
        this.defaultAggregationSpec = defaultAggregationSpec;
    }
}
