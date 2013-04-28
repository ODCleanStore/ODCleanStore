package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.Config;
import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl.ODCSSourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.DistanceMeasureImpl;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.impl.DefaultAggregationConfigurationCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.LabelPropertiesListCache;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMappingCache;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

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

    private final ResolutionFunctionRegistry resolutionFunctionRegistry;


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
        this.resolutionFunctionRegistry = createResolutionFunctionRegistry(globalConfig.getConflictResolutionGroup());
    }

    /**
     * Keyword search query.
     * Triples that contain the given keywords (separated by whitespace) in the object of the triple
     * of type literal are returned.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @param constraints constraints on triples returned in the result
     * @param conflictResolutionPolicy conflict resolution strategies for conflict resolution
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public BasicQueryResult findKeyword(String keywords, QueryConstraintSpec constraints,
            ConflictResolutionPolicy conflictResolutionPolicy) throws QueryExecutionException {

        if (keywords == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "Keywords must not be empty");
        } else if (constraints == null || conflictResolutionPolicy == null) {
            throw new IllegalArgumentException();
        }

        KeywordQueryExecutor queryExecutor = new KeywordQueryExecutor(
                connectionCredentials,
                constraints,
                conflictResolutionPolicy,
                expandedDefaultConfigurationCache.getCachedValue(),
                resolutionFunctionRegistry,
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
     * @param conflictResolutionPolicy conflict resolution strategies
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public BasicQueryResult findURI(String uri, QueryConstraintSpec constraints,
            ConflictResolutionPolicy conflictResolutionPolicy) throws QueryExecutionException {

        if (uri == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "URI must not be empty");
        } else if (constraints == null || conflictResolutionPolicy == null) {
            throw new IllegalArgumentException();
        }

        String trimmedURI = uri.trim();
        String expandedURI = ODCSUtils.isPrefixedName(trimmedURI)
                ? prefixMappingCache.getCachedValue().expandPrefix(trimmedURI)
                : trimmedURI;
        UriQueryExecutor queryExecutor = new UriQueryExecutor(
                connectionCredentials,
                constraints,
                conflictResolutionPolicy,
                expandedDefaultConfigurationCache.getCachedValue(),
                resolutionFunctionRegistry,
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
     * @param conflictResolutionPolicy conflict resolution strategies
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public BasicQueryResult findNamedGraph(String namedGraphURI, QueryConstraintSpec constraints,
            ConflictResolutionPolicy conflictResolutionPolicy) throws QueryExecutionException {

        if (namedGraphURI == null) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_EMPTY_ERR,
                    "Named graph URI must not be empty");
        } else if (constraints == null || conflictResolutionPolicy == null) {
            throw new IllegalArgumentException();
        }

        String trimmedURI = namedGraphURI.trim();
        String expandedURI = ODCSUtils.isPrefixedName(trimmedURI)
                ? prefixMappingCache.getCachedValue().expandPrefix(trimmedURI)
                : trimmedURI;
        NamedGraphQueryExecutor queryExecutor = new NamedGraphQueryExecutor(
                connectionCredentials,
                constraints,
                conflictResolutionPolicy,
                expandedDefaultConfigurationCache.getCachedValue(),
                resolutionFunctionRegistry,
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
                resolutionFunctionRegistry,
                labelPropertiesListCache.getCachedValue(),
                globalConfig.getQueryExecutionGroup());
        return queryExecutor.getMetadata(expandedNamedGraphURI);
    }

    /**
     * Returns factory for for conflict resolution functions.
     * @return resolution function registry initialized with default resolutions functions according to CR configuration.
     */
    private static ResolutionFunctionRegistry createResolutionFunctionRegistry(ConflictResolutionConfig crConfig) {
        DistanceMeasure distanceMeasure = new DistanceMeasureImpl(crConfig.getMaxDateDifference());
        double publisherScoreWeight = crConfig.getPublisherScoreWeight()
                / (crConfig.getPublisherScoreWeight() + crConfig.getNamedGraphScoreWeight());
        SourceConfidenceCalculator sourceConfidenceCalculator = new ODCSSourceConfidenceCalculator(
                crConfig.getScoreIfUnknown(),
                publisherScoreWeight);
        ResolutionFunctionRegistry registry = ResolutionFunctionRegistry.createInitializedWithParams(
                sourceConfidenceCalculator,
                crConfig.getAgreeCoeficient(),
                distanceMeasure);
        return registry;
    }
}
