package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.URIMapping;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.URIMappingImpl;

/**
 * Factory class for ConflictResolver instances.
 *
 * @author Jan Michelfeit
 */
public class ConflictResolverFactory {
    /** Default aggregation settings for conflict resolution. */
    private final AggregationSpec defaultAggregationSpec;

    /** Configuration settings loaded from the global configuration file. */
    private final ConflictResolutionConfig globalConfig;

    /** Prefix of URIs of named graphs where resolved triples are placed. */
    private final String resultGraphPrefix;


    /**
     * Creates a new instance with the given settings passed to newly created ConflictResolver instances.
     * @param resultGraphPrefix prefix of URIs of named graphs where resolved triples are placed
     * @param globalConfig global configuration CR settings
     *        property names must not contain prefixed names
     *       (overridden by aggregationSpec passed to {@link #createResolver()})
     * @param defaultAggregationSpec default aggregation settings for conflict resolution;
     *       (overridden by aggregationSpec passed to {@link #createResolver()})
     */
    public ConflictResolverFactory(
            String resultGraphPrefix,
            ConflictResolutionConfig globalConfig,
            AggregationSpec defaultAggregationSpec) {

        this.globalConfig = globalConfig;
        this.defaultAggregationSpec = defaultAggregationSpec;
        this.resultGraphPrefix = resultGraphPrefix;
    }

    /**
     * Creates a new instance with the given settings passed to newly created ConflictResolver instances.
     * @param resultGraphPrefix prefix of URIs of named graphs where resolved triples are placed
     * @param globalConfig global configuration CR settings
     *        property names must not contain prefixed names
     */
    public ConflictResolverFactory(String resultGraphPrefix, ConflictResolutionConfig globalConfig) {
        this(resultGraphPrefix, globalConfig, new AggregationSpec());
    }

    /**
     * Return a new instance of ConflictResolver.
     * The default returned implementation is not thread-safe.
     * @param aggregationSpec aggregation settings; these have preference over those given in the constructor
     * @param metadata metadata about named graphs of resolved quads
     * @return a ConflictResolver instance
     */
    public ConflictResolver createResolver(
            AggregationSpec aggregationSpec,
            NamedGraphMetadataMap metadata) {

        ConflictResolverSpec crSpec =
                new ConflictResolverSpec(resultGraphPrefix, aggregationSpec, defaultAggregationSpec);
        crSpec.setNamedGraphMetadata(metadata);
        return new ConflictResolverImpl(crSpec, globalConfig);
    }

    /**
     * Return a new instance of ConflictResolver.
     * The default returned implementation is not thread-safe.
     * @param aggregationSpec aggregation settings; these have preference over those given in the constructor
     * @param metadata metadata about named graphs of resolved quads
     * @param uriMapping mapping of URIs to their canonical URI (based on owl:sameAs links)
     * @param preferredURIs set of URIs preferred as canonical URIs
     * @return a ConflictResolver instance
     */
    public ConflictResolver createResolver(
            AggregationSpec aggregationSpec,
            NamedGraphMetadataMap metadata,
            URIMapping uriMapping) {

        ConflictResolverSpec crSpec =
                new ConflictResolverSpec(resultGraphPrefix, aggregationSpec, defaultAggregationSpec);
        crSpec.setNamedGraphMetadata(metadata);
        crSpec.setURIMapping(uriMapping);

        return new ConflictResolverImpl(crSpec, globalConfig);
    }

    /**
     * Return a new instance of ConflictResolver.
     * The default returned implementation is not thread-safe.
     * @param aggregationSpec aggregation settings; these have preference over those given in the constructor
     * @param metadata metadata about named graphs of resolved quads
     * @param sameAsLinks collection of owl:sameAs links to be considered during the conflict resolution process
     * @param preferredURIs set of URIs preferred as canonical URIs
     * @return a ConflictResolver instance
     */
    public ConflictResolver createResolver(
            AggregationSpec aggregationSpec,
            NamedGraphMetadataMap metadata,
            Iterator<Statement> sameAsLinks,
            Set<String> preferredURIs) {

        URIMappingImpl uriMapping = new URIMappingImpl(preferredURIs);
        uriMapping.addLinks(sameAsLinks);
        return createResolver(aggregationSpec, metadata, uriMapping);
    }
}
