/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionStrategyImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.URIMappingImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.EmptyMetadataModel;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * @author Jan Michelfeit
 */
public class ConflictResolverFactory {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    
    public static ConflictResolver create() {
        return new ConflictResolverImpl();
    }

    public static ConflictResolverBuilder configure() {
        return new ConflictResolverBuilder();
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
        private ResolutionStrategy defaultResolutionStrategy = new ResolutionStrategyImpl();
        private Map<URI, ResolutionStrategy> propertyResolutionStrategies = new HashMap<URI, ResolutionStrategy>();
        private Set<String> preferredURIs = Collections.emptySet();
        private URIMapping uriMapping = null;
        private Model metadata = null;
        private String resolvedGraphsURIPrefix = null;
        private ResolutionFunctionRegistry resolutionFunctionRegistry = null;
        private final List<URIPair> sameAsLinks = new ArrayList<URIPair>();

        public ConflictResolver create() {
            return new ConflictResolverImpl(
                    getActualResolutionFunctionRegistry(),
                    defaultResolutionStrategy,
                    propertyResolutionStrategies,
                    getActualURIMapping(),
                    (metadata != null ? metadata : new EmptyMetadataModel()),
                    resolvedGraphsURIPrefix);
        }

        private ResolutionFunctionRegistry getActualResolutionFunctionRegistry() {
            return resolutionFunctionRegistry != null
                    ? resolutionFunctionRegistry
                    : ResolutionFunctionRegistry.createInitialized(); 
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

        public ConflictResolverBuilder setDefaultResolutionFunctionName(String resolutionFunctionName) {
            if (!(defaultResolutionStrategy instanceof ResolutionStrategyImpl)) {
                throw new IllegalStateException(
                        "Cannot set default resolution settings after setDefaultResolutionStrategy() was called.");
            }
            ((ResolutionStrategyImpl) defaultResolutionStrategy).setResolutionFunctionName(resolutionFunctionName);
            return this;
        }
        
        public ConflictResolverBuilder setDefaultResolutionCardinality(EnumCardinality cardinality) {
            if (!(defaultResolutionStrategy instanceof ResolutionStrategyImpl)) {
                throw new IllegalStateException(
                        "Cannot set default resolution settings after setDefaultResolutionStrategy() was called.");
            }
            ((ResolutionStrategyImpl) defaultResolutionStrategy).setCardinality(cardinality);
            return this;
        }
        
        public ConflictResolverBuilder setDefaultResolutionAggregationErrorStrategy(EnumAggregationErrorStrategy strategy) {
            if (!(defaultResolutionStrategy instanceof ResolutionStrategyImpl)) {
                throw new IllegalStateException(
                        "Cannot set default resolution settings after setDefaultResolutionStrategy() was called.");
            }
            ((ResolutionStrategyImpl) defaultResolutionStrategy).setAggregationErrorStrategy(strategy);
            return this;
        }
        
        public ConflictResolverBuilder setDefaultResolutionParams(Map<String, String> params) {
            if (!(defaultResolutionStrategy instanceof ResolutionStrategyImpl)) {
                throw new IllegalStateException(
                        "Cannot set default resolution settings after setDefaultResolutionStrategy() was called.");
            }
            ((ResolutionStrategyImpl) defaultResolutionStrategy).setParams(params);
            return this;
        }
        
        public ConflictResolverBuilder setPropertyResolutionStrategy(URI property, ResolutionStrategy strategy) {
            propertyResolutionStrategies.put(property, strategy);
            return this;
        }
        
        private ResolutionStrategyImpl getPropertyResolutionStrategy(URI property) {
            ResolutionStrategy strategy = propertyResolutionStrategies.get(property);
            if (strategy == null) {
                ResolutionStrategyImpl strategyImpl = new ResolutionStrategyImpl();
                propertyResolutionStrategies.put(property, strategyImpl);
                return strategyImpl;
            } else {
                try {
                    return (ResolutionStrategyImpl) strategy;
                } catch (ClassCastException e) {
                    throw new IllegalStateException(
                            "Cannot set resolution settings for property " + property
                                    + " after setPropertyResolutionStrategy() was called for the property", e);
                }
            }
        }
        
        public ConflictResolverBuilder setPropertyResolutionStrategies(Map<URI, ResolutionStrategy> propertyResolutionStrategies) {
            this.propertyResolutionStrategies = propertyResolutionStrategies;
            return this;
        } 
        
        public ConflictResolverBuilder setPropertyResolutionFunctionName(String property, String resolutionFunctionName) {
            URI propertyURI = VALUE_FACTORY.createURI(property);
            getPropertyResolutionStrategy(propertyURI).setResolutionFunctionName(resolutionFunctionName);
            return this;
        }
        
        public ConflictResolverBuilder setPropertyResolutionCardinality(String property, EnumCardinality cardinality) {
            URI propertyURI = VALUE_FACTORY.createURI(property);
            getPropertyResolutionStrategy(propertyURI).setCardinality(cardinality);
            return this;
        }
        
        public ConflictResolverBuilder setPropertyResolutionAggregationErrorStrategy(String property, EnumAggregationErrorStrategy aggregationErrorStrategy) {
            URI propertyURI = VALUE_FACTORY.createURI(property);
            getPropertyResolutionStrategy(propertyURI).setAggregationErrorStrategy(aggregationErrorStrategy);
            return this;
        }
        
        public ConflictResolverBuilder setPropertyResolutionParams(String property, Map<String, String> params) {
            URI propertyURI = VALUE_FACTORY.createURI(property);
            getPropertyResolutionStrategy(propertyURI).setParams(params);
            return this;
        }
        
        public ConflictResolverBuilder setPreferredCanonicalURIs(Set<String> preferredURIs) {
            this.preferredURIs = preferredURIs;
            return this;
        }
        
        public ConflictResolverBuilder setURIMapping(URIMapping uriMapping) {
            if (!sameAsLinks.isEmpty()) {
                throw new IllegalStateException("Cannot set URIMapping after addSameAsLink() was called");
            }
            this.uriMapping = uriMapping;
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
