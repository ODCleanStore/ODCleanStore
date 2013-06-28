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
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.TresholdResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.VoteResolution;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.WeightedVoteResolution;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * @author Jan Michelfeit
 */
public class ConflictResolverFactory {
    public static ConflictResolver createResolver() {
        return new ConflictResolverImpl();
    }

    public static ConflictResolver createResolver(ResolutionFunctionRegistry resolutionFunctionRegistry,
            ConflictResolutionPolicy conflictResolutionPolicy, URIMapping uriMapping, Model metadata,
            String resolvedGraphsURIPrefix) {
        return new ConflictResolverImpl(resolutionFunctionRegistry, conflictResolutionPolicy, uriMapping,
                metadata, resolvedGraphsURIPrefix);
    }

    public static ConflictResolverBuilder configureResolver() {
        return new ConflictResolverBuilder();
    }
    
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry() {
        return createInitializedResolutionFunctionRegistry(new DummySourceQualityCalculator(), DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(SourceQualityCalculator sourceQualityCalculator) {
        return createInitializedResolutionFunctionRegistry(sourceQualityCalculator, DecidingConflictFQualityCalculator.DEFAULT_AGREE_COEFICIENT, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient) {
        return createInitializedResolutionFunctionRegistry(sourceQualityCalculator, agreeCoefficient, new DistanceMeasureImpl());
    }
    
    public static ResolutionFunctionRegistry createInitializedResolutionFunctionRegistry(SourceQualityCalculator sourceQualityCalculator, double agreeCoefficient, DistanceMeasure distanceMeasure) {
        ResolutionFunctionRegistry registry = new ResolutionFunctionRegistryImpl();
        
        // Deciding resolution functions
        DecidingFQualityCalculator decidingCalculator = new DecidingConflictFQualityCalculator(sourceQualityCalculator, agreeCoefficient, distanceMeasure);
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
        registry.register(TresholdResolution.getName(), new TresholdResolution(decidingCalculator));
        registry.register(VoteResolution.getName(), new VoteResolution(decidingCalculator));
        registry.register(WeightedVoteResolution.getName(), new WeightedVoteResolution(decidingCalculator, sourceQualityCalculator));

        // Moderating mediating resolution functions
        MediatingFQualityCalculator moderatingCalculator = new MediatingModeratingFQualityCalculator(sourceQualityCalculator, distanceMeasure);
        registry.register(AvgResolution.getName(), new AvgResolution(moderatingCalculator));
        registry.register(SumResolution.getName(), new SumResolution(moderatingCalculator));
        
        // Scattering mediating resolution functions
        MediatingScatteringFQualityCalculator scatteringCalculator = new MediatingScatteringFQualityCalculator(sourceQualityCalculator);
        registry.register(ConcatResolution.getName(), new ConcatResolution(scatteringCalculator));
        registry.register(MedianResolution.getName(), new MedianResolution(scatteringCalculator));
        
        // ODCS-specific resolution functions
        registry.register(ODCSLatestResolution.getName(), new ODCSLatestResolution(decidingCalculator));
        
        return registry;
    }
    
    private static class URIPair {
        final String uri1;
        final String uri2;

        public URIPair(String uri1, String uri2) {
            this.uri1 = uri1;
            this.uri2 = uri2;
        }
    }

    public static class ConflictResolverBuilder {
        private ResolutionStrategy defaultResolutionStrategy = null;
        private Map<URI, ResolutionStrategy> propertyResolutionStrategies = Collections.<URI, ResolutionStrategy>emptyMap();
        private Set<String> preferredURIs = Collections.emptySet();
        private URIMapping uriMapping = null;
        private Model metadata = null;
        private String resolvedGraphsURIPrefix = null;
        private ResolutionFunctionRegistry resolutionFunctionRegistry = null;
        private final List<URIPair> sameAsLinks = new ArrayList<URIPair>();

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

        public ConflictResolverBuilder setDefaultResolutionStrategy(ResolutionStrategy resolutionStrategy) {
            this.defaultResolutionStrategy = resolutionStrategy;
            return this;
        }

        public ConflictResolverBuilder setPropertyResolutionStrategies(Map<URI, ResolutionStrategy> propertyResolutionStrategies) {
            this.propertyResolutionStrategies = propertyResolutionStrategies;
            return this;
        }

        public ConflictResolverBuilder setURIMapping(URIMapping uriMapping) {
            if (!sameAsLinks.isEmpty()) {
                throw new IllegalStateException("Cannot set URIMapping after addSameAsLink() was called");
            }
            this.uriMapping = uriMapping;
            return this;
        }

        public ConflictResolverBuilder setPreferredCanonicalURIs(Set<String> preferredURIs) {
            this.preferredURIs = preferredURIs;
            return this;
        }

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

        public ConflictResolverBuilder addLink(String uri1, String uri2) {
            sameAsLinks.add(new URIPair(uri1, uri2));
            return this;
        }

        public ConflictResolverBuilder addLink(URI uri1, URI uri2) {
            sameAsLinks.add(new URIPair(uri1.stringValue(), uri2.stringValue()));
            return this;
        }

        public ConflictResolverBuilder setMetadata(Model metadata) {
            this.metadata = metadata;
            return this;
        }

        public ConflictResolverBuilder setResolvedGraphsURIPrefix(String prefix) {
            this.resolvedGraphsURIPrefix = prefix;
            return this;
        }

        public ConflictResolverBuilder setResolutionFunctionRegistry(ResolutionFunctionRegistry registry) {
            this.resolutionFunctionRegistry = registry;
            return this;
        }
    }
}
