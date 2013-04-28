package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.EmptyMetadataModel;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.GrowingArray;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.AllResolution;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * Default implementation of the conflict resolution process.
 * Non-static methods are not thread-safe (shared {@link #aggregationFactory}).
 * 
 * @author Jan Michelfeit
 */
public class ConflictResolverImpl implements ConflictResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ConflictResolverImpl.class);

    private static final String DEFAULT_RESOLVED_GRAPHS_URI_PREFIX = ODCS.getURI() + "CR/";
    private static final ResolutionStrategy DEFAULT_RESOLUTION_STRATEGY = new ResolutionStrategyImpl(
            AllResolution.getName(),
            EnumCardinality.MULTIVALUE,
            EnumAggregationErrorStrategy.RETURN_ALL);
    private static final int EXPECTED_REDUCTION_FACTOR = 3;

    private Model metadata = null;
    private URIMapping uriMapping = EmptyURIMapping.getInstance();
    private ResolvedStatementFactory resolvedStatementFactory =
            new ResolvedStatementFactoryImpl(DEFAULT_RESOLVED_GRAPHS_URI_PREFIX);
    private ResolutionStrategy defaultResolutionStrategy = DEFAULT_RESOLUTION_STRATEGY;
    private Map<URI, ResolutionStrategy> propertyResolutionStrategy = Collections.emptyMap();
    private ResolutionFunctionRegistry resolutionFunctionRegistry;
    
    private CRContextImpl context;

    // private StatementFilter statementFilter;

    public ConflictResolverImpl() {
        this(ResolutionFunctionRegistry.createInitialized());
    }
    
    public ConflictResolverImpl(ResolutionFunctionRegistry resolutionFunctionRegistry) {
        this.resolutionFunctionRegistry = resolutionFunctionRegistry;
    }

    public ConflictResolverImpl(ResolutionFunctionRegistry resolutionFunctionRegistry, ResolutionStrategy defaultResolutionStrategy) {
        this(resolutionFunctionRegistry);
        setDefaultResolutionStrategy(defaultResolutionStrategy);
    }

    public ConflictResolverImpl(ResolutionFunctionRegistry resolutionFunctionRegistry, ResolutionStrategy defaultResolutionStrategy,
            Map<URI, ResolutionStrategy> propertyResolutionStrategy, URIMapping uriMapping) {
        this(resolutionFunctionRegistry, defaultResolutionStrategy);
        this.propertyResolutionStrategy = propertyResolutionStrategy;
        this.uriMapping = uriMapping;
    }

    public ConflictResolverImpl(ResolutionFunctionRegistry resolutionFunctionRegistry, ResolutionStrategy defaultResolutionStrategy,
            Map<URI, ResolutionStrategy> propertyResolutionStrategy, URIMapping uriMapping, Model metadata) {
        this(resolutionFunctionRegistry, defaultResolutionStrategy, propertyResolutionStrategy, uriMapping);
        this.metadata = metadata;
    }

    public ConflictResolverImpl(ResolutionFunctionRegistry resolutionFunctionRegistry, ResolutionStrategy defaultResolutionStrategy,
            Map<URI, ResolutionStrategy> propertyResolutionStrategy, URIMapping uriMapping, Model metadata,
            String resolvedGraphsURIPrefix) {
        this(resolutionFunctionRegistry, defaultResolutionStrategy, propertyResolutionStrategy, uriMapping, metadata);
        if (resolvedGraphsURIPrefix != null) {
            resolvedStatementFactory = new ResolvedStatementFactoryImpl(resolvedGraphsURIPrefix);
        }
    }

    public void setDefaultResolutionStrategy(ResolutionStrategy newDefaultStrategy) {
        this.defaultResolutionStrategy = fillDefaults(newDefaultStrategy, DEFAULT_RESOLUTION_STRATEGY);
    }

    public void setpropertyResolutionStrategy(Map<URI, ResolutionStrategy> propertyResolutionStrategy) {
        this.propertyResolutionStrategy = propertyResolutionStrategy;
    }

    public void setURIMapping(URIMapping uriMapping) {
        this.uriMapping = uriMapping;
    }

    public void setMetadata(Model metadata) {
        this.metadata = metadata;
    }

    public void setResolvedStatementFactory(ResolvedStatementFactory factory) {
        this.resolvedStatementFactory = factory;
    }

    public void setResolutionFunctionRegistry(ResolutionFunctionRegistry resolutionFunctionRegistry) {
        this.resolutionFunctionRegistry = resolutionFunctionRegistry;
    }
    
    @Override
    public Collection<ResolvedStatement> resolveConflicts(Iterator<Statement> statements) throws ConflictResolutionException {
        GrowingArray<Statement> growingArray = new GrowingArray<Statement>();
        while (statements.hasNext()) {
            growingArray.add(statements.next());
        }
        return resolveConflictsInternal(growingArray.getArray());
    }

    @Override
    public Collection<ResolvedStatement> resolveConflicts(Collection<Statement> statements) throws ConflictResolutionException {
        return resolveConflictsInternal(statements.toArray(new Statement[0]));
    }

    protected Collection<ResolvedStatement> resolveConflictsInternal(Statement[] statements) throws ConflictResolutionException {
        LOG.debug("Resolving conflicts among {} quads.", statements.length);
        long startTime = System.currentTimeMillis();

        // Prepare effective resolution strategy based on per-predicate strategies, default strategy & uri mappings
        Map<URI, ResolutionStrategy> effectiveStrategy = getEffectiveResolutionStrategy();
        
        // Apply owl:sameAs mappings, remove duplicities, sort into clusters of conflicting quads
        ConflictClustersCollection conflictClusters = new ConflictClustersCollection(statements, uriMapping,
                resolvedStatementFactory.getValueFactory());
        initContext(conflictClusters.asModel());

        // Resolve conflicts:
        Collection<ResolvedStatement> result = createResultCollection(conflictClusters.size());
        for (List<Statement> conflictCluster : conflictClusters) {
            // Get resolution strategy
            ResolutionStrategy resolutionStrategy = effectiveStrategy.get(getPredicate(conflictCluster));
            if (resolutionStrategy == null) {
                resolutionStrategy = defaultResolutionStrategy;
            }
            
            // Prepare resolution functions & context
            ResolutionFunction resolutionFunction = getResolutionFunction(resolutionStrategy);
            CRContext context = getContext(conflictCluster, resolutionStrategy);
            Model conflictClusterModel = new SortedListModel(conflictCluster);
            
            // Resolve conflicts & append to result
            Collection<ResolvedStatement> resolvedStatements = resolutionFunction.resolve(conflictClusterModel, context);
            result.addAll(resolvedStatements);
        }

        LOG.debug("Conflict resolution executed in {} ms, resolved to {} quads",
                System.currentTimeMillis() - startTime, result.size());
        return result;
    }

    private void initContext(Model statementsModel) {
        Model metadataModel = metadata != null ? metadata : new EmptyMetadataModel(); 
        context = new CRContextImpl(statementsModel, metadataModel, resolvedStatementFactory);
    }

    private CRContext getContext(List<Statement> conflictCluster, ResolutionStrategy resolutionStrategy) {
        context.setSubject(getSubject(conflictCluster));
        context.setResolutionStrategy(resolutionStrategy);
        return context;
    }

    Map<URI, ResolutionStrategy> getEffectiveResolutionStrategy() {
        Map<URI, ResolutionStrategy> strategy = new HashMap<URI, ResolutionStrategy>();
        for (Entry<URI, ResolutionStrategy> entry : propertyResolutionStrategy.entrySet()) {
            URI mappedURI = uriMapping.mapURI(entry.getKey());
            strategy.put(mappedURI, fillDefaults(entry.getValue(), defaultResolutionStrategy));
        }
        return strategy;
    }

    /**
     * Creates an empty instance of collection that is returned as the output
     * of conflict resolution.
     * @return an empty collection
     */
    private Collection<ResolvedStatement> createResultCollection(int inputSize) {
        return new ArrayList<ResolvedStatement>(inputSize / EXPECTED_REDUCTION_FACTOR);
    }

    private ResolutionFunction getResolutionFunction(ResolutionStrategy resolutionStrategy) throws ResolutionFunctionNotRegisteredException {
        return resolutionFunctionRegistry.get(resolutionStrategy.getResolutionFunctionName());
    }

    private Resource getSubject(List<Statement> conflictCluster) {
        assert !conflictCluster.isEmpty();
        return conflictCluster.get(0).getSubject();
    }

    private URI getPredicate(List<Statement> conflictCluster) {
        assert !conflictCluster.isEmpty();
        return conflictCluster.get(0).getPredicate();
    }
    
    private ResolutionStrategy fillDefaults(ResolutionStrategy strategy, ResolutionStrategy defaultStrategy) {
        String resolutionFunctionName = (strategy.getResolutionFunctionName() == null)
                ? defaultStrategy.getResolutionFunctionName()
                : strategy.getResolutionFunctionName();
        EnumCardinality cardinality = (strategy.getCardinality() == null)
                ? defaultStrategy.getCardinality()
                : strategy.getCardinality();
        EnumAggregationErrorStrategy aggregationErrorStrategy = (strategy.getAggregationErrorStrategy() == null)
                ? defaultStrategy.getAggregationErrorStrategy()
                : strategy.getAggregationErrorStrategy();
        Map<String, String> params = (strategy.getParams() == null)
                ? defaultStrategy.getParams()
                : strategy.getParams(); 
        return new ResolutionStrategyImpl(
                resolutionFunctionName, 
                cardinality,
                aggregationErrorStrategy, 
                params);
    }
}
