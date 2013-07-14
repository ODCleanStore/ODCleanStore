/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolutionPolicyImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.DistanceMeasureImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionFunctionRegistryImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.URIMappingImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.EmptyMetadataModel;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DummySourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.DecidingConflictFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.MediatingModeratingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.impl.MediatingScatteringFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AllResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AnyResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AvgResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.BestSourceResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.CertainResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ChooseSourceResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ConcatResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.FilterResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.LongestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MaxSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MedianResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.MinSourceMetadataValueResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.NoneResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ODCSLatestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ShortestResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.SumResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.TopNResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.ThresholdResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.VoteResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.WeightedVoteResolution;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * Factory and builder class for {@link ConflictResolver} instances.
 * @author Jan Michelfeit
 */
public final class ConflictResolverFactory {
    /**
     * Creates a conflict resolver with default settings and resolution function registry.
     * @return conflict resolver instance
     */
    public static ConflictResolver createResolver() {
        return new ConflictResolverImpl();
    }

    /**
     * Creates a conflict resolver with the given resolution function registry and settings.
     * @param resolutionFunctionRegistry registry for obtaining conflict resolution function implementations
     * @param conflictResolutionPolicy conflict resolution parameters
     * @param uriMapping mapping of URIs to their canonical URI (based on owl:sameAs links)
     * @param metadata additional metadata for use by resolution functions (e.g. source quality etc.)
     * @param resolvedGraphsURIPrefix prefix of graph names where resolved quads are placed
     * @return conflict resolver instance
     */
    public static ConflictResolver createResolver(ResolutionFunctionRegistry resolutionFunctionRegistry,
            ConflictResolutionPolicy conflictResolutionPolicy, URIMapping uriMapping, Model metadata,
            String resolvedGraphsURIPrefix) {
        return new ConflictResolverImpl(resolutionFunctionRegistry, conflictResolutionPolicy, uriMapping,
                metadata, resolvedGraphsURIPrefix);
    }

    /**
     * Returns a builder for conflict resolver.
     * Can be used to configure and create a conflict resolver instance with a fluent interface.
     * @return builder for conflict resolver
     */
    public static ConflictResolverBuilder configureResolver() {
        return new ConflictResolverBuilder();
    }

    /**
     * Returns a resolution function registry initialized with default resolution functions and quality calculators.
     * and default settings (source quality calculator returning 1, default distance measure implementation).
     * @return resolution function registry
     */
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry() {
        return createInitializedResolutionFunctionRegistry(
                new DummySourceQualityCalculator(),
                DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT,
                new DistanceMeasureImpl());
    }

    /**
     * Returns a resolution function registry initialized with default resolution functions and quality calculators
     * using the given data source quality calculator.
     * @param sourceQualityCalculator calculator of source graph quality scores (see {@link SourceQualityCalculator})
     * @return resolution function registry
     */
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(
            SourceQualityCalculator sourceQualityCalculator) {

        return createInitializedResolutionFunctionRegistry(
                sourceQualityCalculator,
                DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT,
                new DistanceMeasureImpl());
    }

    /**
     * Returns a resolution function registry initialized with default resolution functions and quality calculators
     * using the given data source quality calculator.
     * @param sourceQualityCalculator calculator of source graph quality scores (see {@link SourceQualityCalculator})
     * @param agreeCoefficient agree coefficient for quality calculation 
     *      (see {@link DecidingConflictFQualityCalculator}).
     * @return resolution function registry
     */
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(
            SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient) {
        return createInitializedResolutionFunctionRegistry(sourceQualityCalculator, agreeCoefficient, new DistanceMeasureImpl());
    }

    /**
     * Returns a resolution function registry initialized with default resolution functions and quality calculators
     * using the given data source quality calculator and distance measure.
     * @param sourceQualityCalculator calculator of source graph quality scores (see {@link SourceQualityCalculator})
     * @param agreeCoefficient agree coefficient for quality calculation (see {@link DecidingConflictFQualityCalculator})
     * @param distanceMeasure distance measure between RDF nodes used during quality calculation
     * @return resolution function registry
     */
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(
            SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient, DistanceMeasure distanceMeasure) {
        ResolutionFunctionRegistry registry = new ResolutionFunctionRegistryImpl();

        // Deciding resolution functions
        DecidingFQualityCalculator decidingCalculator = new DecidingConflictFQualityCalculator(sourceQualityCalculator,
                agreeCoefficient, distanceMeasure);
        registry.register(AllResolution.getName(), new AllResolution(decidingCalculator));
        registry.register(AnyResolution.getName(), new AnyResolution(decidingCalculator));
        registry.register(BestResolution.getName(), new BestResolution(decidingCalculator));
        registry.register(BestSourceResolution.getName(), new BestSourceResolution(decidingCalculator, sourceQualityCalculator));
        registry.register(CertainResolution.getName(), new CertainResolution(decidingCalculator));
        registry.register(FilterResolution.getName(), new FilterResolution(decidingCalculator));
        registry.register(LongestResolution.getName(), new LongestResolution(decidingCalculator));
        registry.register(MaxResolution.getName(), new MaxResolution(decidingCalculator));
        registry.register(MaxSourceMetadataValueResolution.getName(), new MaxSourceMetadataValueResolution(decidingCalculator));
        registry.register(MinResolution.getName(), new MinResolution(decidingCalculator));
        registry.register(MinSourceMetadataValueResolution.getName(), new MinSourceMetadataValueResolution(decidingCalculator));
        registry.register(NoneResolution.getName(), new NoneResolution(decidingCalculator));
        registry.register(ChooseSourceResolution.getName(), new ChooseSourceResolution(decidingCalculator));
        registry.register(ShortestResolution.getName(), new ShortestResolution(decidingCalculator));
        registry.register(TopNResolution.getName(), new TopNResolution(decidingCalculator));
        registry.register(ThresholdResolution.getName(), new ThresholdResolution(decidingCalculator));
        registry.register(VoteResolution.getName(), new VoteResolution(decidingCalculator));
        registry.register(WeightedVoteResolution.getName(), new WeightedVoteResolution(decidingCalculator,
                sourceQualityCalculator));

        // Moderating mediating resolution functions
        MediatingFQualityCalculator moderatingCalculator = new MediatingModeratingFQualityCalculator(sourceQualityCalculator,
                distanceMeasure);
        registry.register(AvgResolution.getName(), new AvgResolution(moderatingCalculator));
        registry.register(SumResolution.getName(), new SumResolution(moderatingCalculator));

        // Scattering mediating resolution functions
        MediatingScatteringFQualityCalculator scatteringCalculator = new MediatingScatteringFQualityCalculator(
                sourceQualityCalculator);
        registry.register(ConcatResolution.getName(), new ConcatResolution(scatteringCalculator));
        registry.register(MedianResolution.getName(), new MedianResolution(scatteringCalculator));

        // ODCS-specific resolution functions
        registry.register(ODCSLatestResolution.getName(), new ODCSLatestResolution(decidingCalculator));

        return registry;
    }

    /** Pair of URIs. */ 
    private static class URIPair {
        private final String uri1;
        private final String uri2;

        public URIPair(String uri1, String uri2) {
            this.uri1 = uri1;
            this.uri2 = uri2;
        }
    }

    /**
     * Builder class for a conflict resolver. 
     * Can be used to set all settings for the resolver with a fluent interface. 
     */
    public static class ConflictResolverBuilder {
        private ResolutionStrategy defaultResolutionStrategy = null;
        private Map<URI, ResolutionStrategy> propertyResolutionStrategies = Collections.<URI, ResolutionStrategy>emptyMap();
        private Set<String> preferredURIs = Collections.emptySet();
        private URIMapping uriMapping = null;
        private Model metadata = null;
        private String resolvedGraphsURIPrefix = null;
        private ResolutionFunctionRegistry resolutionFunctionRegistry = null;
        private final List<URIPair> sameAsLinks = new ArrayList<URIPair>();

        /**
         * Returns a conflict resolver instance configured with the settings set on this builder instance so far. 
         * @return conflict resolver instance
         */
        public ConflictResolver create() {
            return new ConflictResolverImpl(
                    getActualResolutionFunctionRegistry(),
                    new ConflictResolutionPolicyImpl(defaultResolutionStrategy, propertyResolutionStrategies),
                    getActualURIMapping(),
                    (metadata != null ? metadata : new EmptyMetadataModel()),
                    resolvedGraphsURIPrefix);
        }

        private ResolutionFunctionRegistry getActualResolutionFunctionRegistry() {
            return resolutionFunctionRegistry != null
                    ? resolutionFunctionRegistry
                    : createInitializedResolutionFunctionRegistry();
        }

        private URIMapping getActualURIMapping() {
            if (uriMapping != null) {
                return uriMapping;
            }
            URIMappingImpl actualURIMapping = new URIMappingImpl(preferredURIs);
            for (URIPair pair : sameAsLinks) {
                actualURIMapping.addLink(pair.uri1, pair.uri2);
            }
            return actualURIMapping;
        }

        /**
         * Sets default conflict resolution strategy (used if not overridden with a per-property strategy). 
         * @param resolutionStrategy default conflict resolution strategy
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setDefaultResolutionStrategy(ResolutionStrategy resolutionStrategy) {
            this.defaultResolutionStrategy = resolutionStrategy;
            return this;
        }

        /**
         * Sets per-property conflict resolution strategies). 
         * @param propertyResolutionStrategies map of resolution strategies as property URI -> resolution strategy
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setPropertyResolutionStrategies(
                Map<URI, ResolutionStrategy> propertyResolutionStrategies) {
            this.propertyResolutionStrategies = propertyResolutionStrategies;
            return this;
        }

        /**
         * Sets mapping of URIs to their canonical URI. 
         * An alternative to calling this method are methods {@link #setPreferredCanonicalURIs(Set)} 
         * and {@link #addSameAsLinks(Iterator)}.
         * @param uriMapping mapping to canonical URIs
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setURIMapping(URIMapping uriMapping) {
            if (!sameAsLinks.isEmpty()) {
                throw new IllegalStateException("Cannot set URIMapping after addSameAsLink() was called");
            }
            this.uriMapping = uriMapping;
            return this;
        }

        /**
         * Sets preferred canonical URIs for uri mapping created from owl:sameAs links given 
         * by {@link #addSameAsLinks(Iterator)}. 
         * An alternative to using this method is {@link #setURIMapping(URIMapping)}.
         * @see #addSameAsLinks(Iterator)
         * @param preferredURIs set of URIs preferred for canonical URIs
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setPreferredCanonicalURIs(Set<String> preferredURIs) {
            this.preferredURIs = preferredURIs;
            return this;
        }

        /**
         * Sets owl:sameAs links used to create URI mapping. 
         * An alternative to using this method is {@link #setURIMapping(URIMapping)}. 
         * @see #setPreferredCanonicalURIs(Set)
         * @param sameAsLinks iterator over statements with owl:sameAs as predicate
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder addSameAsLinks(Iterator<Statement> sameAsLinks) {
            if (uriMapping != null) {
                throw new IllegalStateException("Cannot add more owl:sameAs links after setURIMapping() was called");
            }
            while (sameAsLinks.hasNext()) {
                Statement triple = sameAsLinks.next();
                if (!triple.getPredicate().stringValue().equals(OWL.sameAs)) {
                    continue;
                }
                if (!(triple.getSubject() instanceof URI) || !(triple.getObject() instanceof URI)) {
                    // Ignore sameAs links between everything but URI resources; see owl:sameAs syntax
                    // at see http://www.w3.org/TR/2004/REC-owl-semantics-20040210/syntax.html
                    continue;
                }
                String subjectURI = triple.getSubject().stringValue();
                String objectURI = triple.getObject().stringValue();
                addLink(subjectURI, objectURI);
            }
            return this;
        }
        
        /**
         * Adds an owl:sameAs link between the two given URIs.
         * @see #addSameAsLinks(Iterator) 
         * @param uri1 URI
         * @param uri2 URI
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder addLink(String uri1, String uri2) {
            sameAsLinks.add(new URIPair(uri1, uri2));
            return this;
        }

        /**
         * Adds an owl:sameAs link between the two given URIs.
         * @see #addSameAsLinks(Iterator) 
         * @param uri1 URI
         * @param uri2 URI
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder addLink(URI uri1, URI uri2) {
            sameAsLinks.add(new URIPair(uri1.stringValue(), uri2.stringValue()));
            return this;
        }

        /**
         * Sets additional metadata for use during conflict resolution and quality calculation.  
         * @param metadata metadata as an RDF model
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setMetadata(Model metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Sets prefix of graph names where resolved quads are placed. 
         * @param prefix prefix of graph names where resolved quads are placed
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setResolvedGraphsURIPrefix(String prefix) {
            this.resolvedGraphsURIPrefix = prefix;
            return this;
        }

        /**
         * Sets registry of conflict resolution functions to be used.
         * The registry is used by conflict resolver to obtain appropriate conflict resolution function implemenations. 
         * @param registry registry for obtaining conflict resolution function implementations
         * @return this builder (can be used for configuration with in a fluent way)
         */
        public ConflictResolverBuilder setResolutionFunctionRegistry(ResolutionFunctionRegistry registry) {
            this.resolutionFunctionRegistry = registry;
            return this;
        }
    }

    private ConflictResolverFactory() {
        // hidden constructor
    }
}
