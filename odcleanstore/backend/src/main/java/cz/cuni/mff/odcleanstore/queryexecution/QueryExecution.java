package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.Config;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.impl.DefaultAggregationConfigurationCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMappingCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.QueryExecutionHelper;
import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * Access point (facade) of the Query Execution component.
 * Provides access to methods provided by each QueryExecutor.
 *
 * The purpose of Query Execution is to load triples relevant for the given query from the clean
 * database, apply conflict resolution to it, converts the result (collection of CRQuads)
 * to plain quads and return the result.
 *
 * Methods of this class are thread-safe.
 *
 * @author Jan Michelfeit
 */
public class QueryExecution {
    /** Connection settings for the SPARQL endpoint that will be queried. */
    private final ConnectionCredentials sparqlEndpoint;

    /** Default aggregation settings for conflict resolution (loaded from database). */
    private DefaultAggregationConfigurationCache expandedDefaultConfigurationCache;

    /** Prefix mappings. */
    private final PrefixMappingCache prefixMappingCache;

    /**
     * Container for QE & CR configuration loaded from the global configuration file.
     */
    private Config globalConfig;

    /**
     * Creates a new instance of QueryExecution.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param globalConfig container for QE & CR configuration loaded from the global configuration file
     */
    public QueryExecution(ConnectionCredentials sparqlEndpoint, Config globalConfig) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.globalConfig = globalConfig;
        this.prefixMappingCache = new PrefixMappingCache(sparqlEndpoint);
        this.expandedDefaultConfigurationCache =
                new DefaultAggregationConfigurationCache(sparqlEndpoint, prefixMappingCache);
    }

    /**
     * Keyword search query.
     * Triples that contain the given keywords (separated by whitespace) in the object of the triple
     * of type literal are returned.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution; may contain properties as  prefixed names
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public QueryResult findKeyword(String keywords, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        AggregationSpec expandedAggregationSpec = QueryExecutionHelper.expandPropertyNames(
                aggregationSpec, prefixMappingCache.getCachedValue());
        KeywordQueryExecutor queryExecutor = new KeywordQueryExecutor(sparqlEndpoint, constraints,
                expandedAggregationSpec, createConflictResolverFactory(), globalConfig.getQueryExecutionGroup());
        return queryExecutor.findKeyword(keywords);
    }

    /**
     * URI search query.
     * Triples that contain the given URI as their subject or object are returned.
     *
     * @param uri searched URI; may be a prefixed name
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution; may contain properties as  prefixed names
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public QueryResult findURI(String uri, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        String expandedURI = Utils.isPrefixedName(uri) ? prefixMappingCache.getCachedValue().expandPrefix(uri) : uri;
        AggregationSpec expandedAggregationSpec = QueryExecutionHelper.expandPropertyNames(
                aggregationSpec, prefixMappingCache.getCachedValue());
        UriQueryExecutor queryExecutor = new UriQueryExecutor(sparqlEndpoint, constraints, expandedAggregationSpec,
                createConflictResolverFactory(), globalConfig.getQueryExecutionGroup());
        return queryExecutor.findURI(expandedURI);
    }

    /**
     * Creates a new instance of ConflictResolverFactory using the correct default settings.
     * A new instance should be created every time in order to reflect the current (cached) settings.
     * @throws QueryExecutionException default settings cannot be loaded
     * @return a new ConflictResolverFactory instance
     */
    private ConflictResolverFactory createConflictResolverFactory() throws QueryExecutionException {
        AggregationSpec defaultConfiguration = expandedDefaultConfigurationCache.getCachedValue();
        String resultGraphPrefix = globalConfig.getQueryExecutionGroup().getResultGraphURIPrefix().toString();
        return new ConflictResolverFactory(resultGraphPrefix,
                globalConfig.getConflictResolutionGroup(), defaultConfiguration);
    }
}
