package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.Config;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.impl.DefaultAggregationConfigurationCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.LabelPropertiesListCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMappingCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.QueryExecutionHelper;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecution.class);

    /** JDBC connection settings for the SPARQL endpoint that will be queried. */
    private final JDBCConnectionCredentials connectionCredentials;

    /** Default aggregation settings for conflict resolution (loaded from database). */
    private DefaultAggregationConfigurationCache expandedDefaultConfigurationCache;

    /** Prefix mappings. */
    private final PrefixMappingCache prefixMappingCache;

    /** Properties designating a human-readable label formatted to a string for use in a SPARQL query, with caching. */
    protected LabelPropertiesListCache labelPropertiesListCache;

    /**
     * Container for QE & CR configuration loaded from the global configuration file.
     */
    private Config globalConfig;

    /**
     * Creates a new instance of QueryExecution.
     * @param connectionCredentials JDBC connection settings for the SPARQL endpoint that will be queried
     * @param globalConfig container for QE & CR configuration loaded from the global configuration file
     */
    public QueryExecution(JDBCConnectionCredentials connectionCredentials, Config globalConfig) {
        LOG.info("Initializing QueryExecution instance");
        this.connectionCredentials = connectionCredentials;
        this.globalConfig = globalConfig;
        this.prefixMappingCache = new PrefixMappingCache(connectionCredentials);
        this.labelPropertiesListCache = new LabelPropertiesListCache(connectionCredentials, prefixMappingCache);
        this.expandedDefaultConfigurationCache =
                new DefaultAggregationConfigurationCache(connectionCredentials, prefixMappingCache);
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
    public BasicQueryResult findKeyword(String keywords, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        if (keywords == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "Keywords must not be empty");
        } else if (constraints == null || aggregationSpec == null) {
            throw new IllegalArgumentException();
        }

        AggregationSpec expandedAggregationSpec = QueryExecutionHelper.expandPropertyNames(
                aggregationSpec, prefixMappingCache.getCachedValue());
        KeywordQueryExecutor queryExecutor = new KeywordQueryExecutor(
                connectionCredentials,
                constraints,
                expandedAggregationSpec,
                createConflictResolverFactory(),
                labelPropertiesListCache.getCachedValue(),
                globalConfig.getQueryExecutionGroup());
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
    public BasicQueryResult findURI(String uri, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        if (uri == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "URI must not be empty");
        } else if (constraints == null || aggregationSpec == null) {
            throw new IllegalArgumentException();
        }

        String trimmedURI = uri.trim();
        String expandedURI = ODCSUtils.isPrefixedName(trimmedURI)
                ? prefixMappingCache.getCachedValue().expandPrefix(trimmedURI)
                : trimmedURI;
        AggregationSpec expandedAggregationSpec = QueryExecutionHelper.expandPropertyNames(
                aggregationSpec, prefixMappingCache.getCachedValue());
        UriQueryExecutor queryExecutor = new UriQueryExecutor(
                connectionCredentials,
                constraints,
                expandedAggregationSpec,
                createConflictResolverFactory(),
                labelPropertiesListCache.getCachedValue(),
                globalConfig.getQueryExecutionGroup());
        return queryExecutor.findURI(expandedURI);
    }

    /**
     * Named graph search query.
     * All triples from the given named graph are returned.
     *
     * @param namedGraphURI URI of the requested named graph; may be a prefixed name
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution; may contain properties as prefixed names
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public BasicQueryResult findNamedGraph(String namedGraphURI, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        if (namedGraphURI == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "Named graph URI must not be empty");
        } else if (constraints == null || aggregationSpec == null) {
            throw new IllegalArgumentException();
        }

        String trimmedURI = namedGraphURI.trim();
        String expandedURI = ODCSUtils.isPrefixedName(trimmedURI)
                ? prefixMappingCache.getCachedValue().expandPrefix(trimmedURI)
                : trimmedURI;
        AggregationSpec expandedAggregationSpec = QueryExecutionHelper.expandPropertyNames(
                aggregationSpec, prefixMappingCache.getCachedValue());
        NamedGraphQueryExecutor queryExecutor = new NamedGraphQueryExecutor(
                connectionCredentials,
                constraints,
                expandedAggregationSpec,
                createConflictResolverFactory(),
                labelPropertiesListCache.getCachedValue(),
                globalConfig.getQueryExecutionGroup());
        return queryExecutor.getNamedGraph(expandedURI);
    }


    /**
     * Named graph provenance metadata query.
     * Metadata about a given named graph are returned.
     * The result quads contain RDF/XML provenance metadata provided to the input webservice, other metadata are
     * stored in NamedGraphMetadataMap.
     *
     * @param namedGraphURI URI of a named graph; may be a prefixed name
     * @return result of the query
     * @throws QueryExecutionException exception
     */
    public MetadataQueryResult findNamedGraphMetadata(String namedGraphURI) throws QueryExecutionException {
        if (namedGraphURI == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "Named graph URI must not be empty");
        }

        String trimmedURI = namedGraphURI.trim();
        String expandedNamedGraphURI = ODCSUtils.isPrefixedName(trimmedURI)
                ? prefixMappingCache.getCachedValue().expandPrefix(trimmedURI)
                : trimmedURI;
        MetadataQueryExecutor queryExecutor = new MetadataQueryExecutor(
                connectionCredentials,
                createConflictResolverFactory(),
                labelPropertiesListCache.getCachedValue(),
                globalConfig.getQueryExecutionGroup());
        return queryExecutor.getMetadata(expandedNamedGraphURI);
    }

    /**
     * Creates a new instance of ConflictResolverFactory using the correct default settings.
     * A new instance should be created every time in order to reflect the current (cached) settings.
     * @throws QueryExecutionException default settings cannot be loaded
     * @return a new ConflictResolverFactory instance
     */
    private ConflictResolverFactory createConflictResolverFactory() throws QueryExecutionException {
        AggregationSpec defaultConfiguration = expandedDefaultConfigurationCache.getCachedValue();
        String resultGraphPrefix =
                globalConfig.getQueryExecutionGroup().getResultDataURIPrefix().toString() + ODCSInternal.queryResultGraphUriInfix;
        return new ConflictResolverFactory(resultGraphPrefix,
                globalConfig.getConflictResolutionGroup(), defaultConfiguration);
    }
}
