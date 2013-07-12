package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.QueryExecutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.util.LimitedURIListBuilder;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The base class of query executors.
 *
 * Each query executor loads triples relevant for the query and metadata from the clean database, applies
 * conflict resolution to it and returns a holder of the result quads and metadata.
 *
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorBase.class);

    /** Maximum allowed length of a URI query. */
    public static final int MAX_URI_LENGTH = 1024;

    /**
     * Only named graph having URI not starting with this prefix can be included in query result.
     * @see ODCSInternal#hiddenGraphPrefix
     */
    protected static final String ENGINE_TEMP_GRAPH_PREFIX = ODCSInternal.hiddenGraphPrefix;

    /**
     * (Debug) Only named graph having URI starting with this prefix can be included in query result.
     * If the value is null, there is now restriction on named graph URIs.
     * This constant is only for debugging purposes and should be null in production environment.
     */
    private static final String GRAPH_PREFIX_FILTER = null; // "http://odcs.mff.cuni.cz/namedGraph/qe-test/";

    /**
     * Maximum number of properties with explicit aggregation settings.
     * This limit is imposed because all of them might be listed in a query.
     * @see UriQueryExecutor#getSameAsLinks()
     */
    private static final long MAX_PROPERTY_SETTINGS_SIZE = 500;

    /**
     * Maximum length of owl:sameAs path considered when searching for synonyms of a URI.
     * @see UriQueryExecutor#addSameAsLinksForURI(String, Collection)
     */
    private static final int MAX_SAMEAS_PATH_LENGTH = 30;

    /**
     * Maximum number of values in a generated argument for the "?var IN (...)" SPARQL construct .
     */
    private static final int MAX_QUERY_LIST_LENGTH = 25;

    /**
     * A {@link URI} representing the owl:sameAs predicate.
     */
    protected static final URI SAME_AS_PROPERTY = ValueFactoryImpl.getInstance().createURI(OWL.sameAs);

    private static final URI PUBLISHED_BY_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.publishedBy);

    /**
     * Value factory instance.
     */
    protected static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    /**
     * SPARQL snippet restricting result to ?graph having at least the given score.
     * Even though the score must be present, the pattern wouldn't work without OPTIONAL
     * (probably due to Virtuoso inference processing).
     * Must be formatted with the score as an argument.
     */
    private static final String SCORE_FILTER_CLAUSE = " OPTIONAL { ?graph <" + ODCS.score + "> ?_score }"
            + " FILTER(?_score >= %f)";

    /**
     * SPARQL snippet restricting result to ?graph having at least the given inserted at date.
     * Even though the score must be present, the pattern wouldn't work without OPTIONAL
     * (probably due to Virtuoso inference processing).
     * Must be formatted with the date given as an argument.
     */
    private static final String INSERTED_AT_FILTER_CLAUSE = " OPTIONAL"
            + " { ?graph <" + ODCS.insertedAt + "> ?_insertedAt }"
            + " FILTER(?_insertedAt >= \"%s\"^^<" + XMLSchema.dateTimeType + ">)";

    /**
     * SPARQL snippet restricting a variable to start with the given string.
     * Must be formatted with a string argument.
     */
    private static final String PREFIX_FILTER_CLAUSE = " FILTER (bif:starts_with(str(?%s), '%s')) ";

    /**
     * SPARQL snippet restricting a variable NOT to start with the given string.
     * Must be formatted with a string argument.
     */
    private static final String PREFIX_FILTER_CLAUSE_NEGATIVE = " FILTER (!bif:starts_with(str(?%s), '%s')) ";

    /**
     * SPARQL query for retrieving all synonyms (i.e. resources connected by an owl:sameAs path) of a given URI.
     * Must be formatted with arguments: (1) URI, (2) maximum length of owl:sameAs path considered, (3) limit.
     * @see #MAX_SAMEAS_PATH_LENGTH
     *      TODO: ignore links from graphs hidden by Engine?
     */
    private static final String URI_SYNONYMS_QUERY =
            "SELECT ?r ?syn"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT ?r ?syn"
            + "\n     WHERE {"
            + "\n       { ?r owl:sameAs ?syn }"
            + "\n       UNION"
            + "\n       { ?syn owl:sameAs ?r }"
            + "\n     }"
            + "\n   }"
            + "\n   OPTION (TRANSITIVE, t_in(?r), t_out(?syn), t_distinct, t_min(1), t_max(%2$d))"
            + "\n   FILTER (?r = <%1$s>)"
            + "\n }"
            + "\n LIMIT %3$d";

    /**
     * SPARQL query that gets the publisher scores for the given publishers.
     *
     * Must be formatted with arguments: (1) non-empty comma separated list of publisher URIs, (2) limit.
     */
    private static final String PUBLISHER_SCORE_QUERY =
            "CONSTRUCT"
            + "\n   { ?publishedBy <" + ODCS.publisherScore + "> ?score }"
            + "\n WHERE {"
            + "\n   ?publishedBy <" + ODCS.publisherScore + "> ?score."
            + "\n   FILTER (?publishedBy IN (%1$s))"
            + "\n }"
            + "\n LIMIT %2$d";

    /**
     * SPARQL query that retrieves labels for given resources.
     * Must be formatted with arguments: (1) non-empty comma separated list of resource URIs, (2) list of
     * label properties, (3) ?labelGraph prefix filter, (4) limit.
     */
    private static final String LABELS_QUERY =
            "DEFINE input:same-as \"yes\""
            + "\n SELECT ?labelGraph ?r ?labelProp ?label WHERE {{"
            + "\n SELECT DISTINCT ?labelGraph ?r ?labelProp ?label"
            + "\n WHERE {"
            + "\n   GRAPH ?labelGraph {"
            + "\n     ?r ?labelProp ?label"
            + "\n     FILTER (?r IN (%1$s))"
            + "\n     FILTER (?labelProp IN (%2$s))"
            + "\n   }"
            + "\n   %3$s"
            + "\n }"
            + "\n LIMIT %4$d"
            + "\n }}";

    /**
     * Returns a SPARQL snippet restricting a named graph URI referenced by the given variable to GRAPH_PREFIX_FILTER.
     * Returns an empty string if GRAPH_PREFIX_FILTER is null.
     * @see #GRAPH_PREFIX_FILTER
     * @param graphVariable SPARQL variable name
     * @return SPARQL query snippet
     */
    protected static String getGraphPrefixFilter(String graphVariable) {
        String result = String.format(Locale.ROOT, PREFIX_FILTER_CLAUSE_NEGATIVE, graphVariable, ENGINE_TEMP_GRAPH_PREFIX);
        if (GRAPH_PREFIX_FILTER != null) {
            result += String.format(Locale.ROOT, PREFIX_FILTER_CLAUSE, graphVariable, GRAPH_PREFIX_FILTER);
        }
        return result;
    }

    /**
     * @see {@link #getGraphFilterClause()}
     * @param constraints constraints on triples returned in the result
     * @return SPARQL query snippet
     */
    private static CharSequence buildGraphFilterClause(QueryConstraintSpec constraints) {
        if (constraints.getMinScore() == null && constraints.getOldestTime() == null) {
            return getGraphPrefixFilter("graph");
        }
        StringBuilder sb = new StringBuilder();
        if (constraints.getMinScore() != null) {
            sb.append(String.format(Locale.ROOT, SCORE_FILTER_CLAUSE, constraints.getMinScore()));
        }
        if (constraints.getOldestTime() != null) {
            java.sql.Timestamp oldestTime = new Timestamp(constraints.getOldestTime().getTime());
            sb.append(String.format(Locale.ROOT, INSERTED_AT_FILTER_CLAUSE, oldestTime.toString()));
        }
        sb.append(getGraphPrefixFilter("graph"));
        return sb;
    }

    /** Connection settings for the SPARQL endpoint that will be queried. */
    protected final JDBCConnectionCredentials connectionCredentials;

    /** Constraints on triples returned in the result. */
    protected final QueryConstraintSpec constraints;

    /** Properties designating a human-readable label formatted to a string for use in a SPARQL query. */
    protected String labelPropertiesList;

    /** Global QE configuration settings. */
    protected final QueryExecutionConfig globalConfig;

    /** Database connection. */
    private RepositoryConnection connection;

    /** Sesame repository for representing Virtuoso clean database instance. */
    private Repository virtuosoRepository;

    /**
     * Cached graph filter SPARQL snippet.
     * Depends only on settings immutable during the instance lifetime and thus can be cached.
     */
    private CharSequence graphFilterClause;

    /** Conflict resolution strategies for conflict resolution. */
    protected final ConflictResolutionPolicy conflictResolutionPolicy;

    /** Default conflict resolution strategies defined by administrator. */
    protected final ConflictResolutionPolicy defaultResolutionPolicy;

    /** Maximum number of triples returned by each database query (the overall result size may be larger). */
    protected final Long maxLimit;

    /** Factory for resolution functions. */
    private final ResolutionFunctionRegistry resolutionFunctionRegistry;

    /**
     * Creates a new instance of QueryExecutorBase.
     * @param connectionCredentials connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param conflictResolutionPolicy conflict resolution strategies for conflict resolution;
     * @param defaultResolutionPolicy default conflict resolution strategies defined by administrator
     * @param defaultResolutionPolicy
     * @param resolutionFunctionRegistry factory for resolution functions
     * @param labelPropertiesList list of label properties formatted as a string for use in a query
     * @param globalConfig global conflict resolution settings;
     *        values needed in globalConfig are the following:
     *        <dl>
     *        <dt>maxQueryResultSize
     *        <dd>Maximum number of triples returned by each database query (the overall result size may be larger).
     *        <dt>resultGraphPrefix
     *        <dd>Prefix of named graphs where the resulting triples are placed.
     *        </dl>
     */
    protected QueryExecutorBase(JDBCConnectionCredentials connectionCredentials, QueryConstraintSpec constraints,
            ConflictResolutionPolicy conflictResolutionPolicy, ConflictResolutionPolicy defaultResolutionPolicy,
            ResolutionFunctionRegistry resolutionFunctionRegistry, String labelPropertiesList,
            QueryExecutionConfig globalConfig) {
        this.connectionCredentials = connectionCredentials;
        this.constraints = constraints;
        this.conflictResolutionPolicy = conflictResolutionPolicy;
        this.defaultResolutionPolicy = defaultResolutionPolicy;
        this.globalConfig = globalConfig;
        this.maxLimit = globalConfig.getMaxQueryResultSize();
        this.labelPropertiesList = labelPropertiesList;
        this.resolutionFunctionRegistry = resolutionFunctionRegistry;
    }

    /**
     * Returns a database connection.
     * The connection is shared within this instance until it is closed.
     * @return database connection
     * @throws ConnectionException database connection error
     */
    protected RepositoryConnection getConnection() throws ConnectionException {
        if (connection == null) {
            closeConnectionQuietly(); // just in case
            virtuosoRepository = new VirtuosoRepository(
                    connectionCredentials.getConnectionString(),
                    connectionCredentials.getUsername(),
                    connectionCredentials.getPassword());
            try {
                virtuosoRepository.initialize();
                connection = virtuosoRepository.getConnection();
            } catch (RepositoryException e) {
                throw new ConnectionException(e);

            }
        }
        return connection;
    }

    /**
     * Closes an opened database connection, if any.
     */
    protected void closeConnectionQuietly() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
            if (virtuosoRepository != null) {
                virtuosoRepository.shutDown();
                virtuosoRepository = null;
            }
        } catch (RepositoryException e) {
            // do nothing
        }
    }

    /**
     * Returns a SPARQL query snippet restricting results to ?graph variable containing a named graph URI to
     * current query constraints. The value is cached.
     * @return SPARQL query snippet
     */
    protected CharSequence getGraphFilterClause() {
        if (graphFilterClause == null) {
            graphFilterClause = buildGraphFilterClause(constraints);
        }
        return graphFilterClause;
    }

    /**
     * Check whether aggregation settings and query constraints are valid.
     * For now, checks only compliance with {@link #MAX_PROPERTY_SETTINGS_SIZE}.
     * @throws QueryExecutionException aggregation settings or query constraints are invalid
     */
    protected void checkValidSettings() throws QueryExecutionException {
        // Check that the size of settings is reasonable - the query size may depend on it
        int settingsPropertyCount = conflictResolutionPolicy.getPropertyResolutionStrategies().size();
        if (settingsPropertyCount > MAX_PROPERTY_SETTINGS_SIZE) {
            throw new QueryExecutionException(EnumQueryError.QUERY_TOO_LONG, ODCSErrorCodes.QE_INPUT_FORMAT_ERR,
                    "Too many explicit property settings.");
        }

        // Log a warning if using this debug option
        if (GRAPH_PREFIX_FILTER != null) {
            LOG.warn("Query is limited to named graph starting with '{}'", GRAPH_PREFIX_FILTER);
        }
    }

    /**
     * Execute the given SPARQL SELECT and constructs a collection of quads from the result.
     * The query must contain four variables in the result, exactly in this order: named graph, subject,
     * property, object
     * @param sparqlQuery a SPARQL SELECT query with four variables in the result: named graph, subject,
     *        property, object (exactly in this order).
     * @param debugName named of the query used for debug log
     * @return result of the query as a collection of quads
     * @throws DatabaseException database error
     */
    protected Collection<Statement> getQuadsFromQuery(String sparqlQuery, String debugName) throws DatabaseException {
        Collection<Statement> quads = new ArrayList<Statement>();
        TupleQueryResult resultSet = null;
        try {
            long startTime = System.currentTimeMillis();
            resultSet = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate();
            LOG.debug("Query Execution: {} query took {} ms", debugName, System.currentTimeMillis() - startTime);

            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();
                Statement quad = VALUE_FACTORY.createStatement(
                        (Resource) bindingSet.getValue("s"),
                        (URI) bindingSet.getValue("p"),
                        bindingSet.getValue("o"),
                        (Resource) bindingSet.getValue("graph"));
                quads.add(quad);
            }

            LOG.debug("Query Execution: {} in {} ms", debugName, System.currentTimeMillis() - startTime);
            return quads;
        } catch (OpenRDFException e) {
            throw new QueryException(e);
        } finally {
            closeResultSetQuietly(resultSet);
        }
    }

    /**
     * Extract named graph metadata from the result of the given SPARQL SELECT query.
     * The query must contain three variables in the result, exactly in this order: named graph, property, value
     * @param sparqlQuery a SPARQL SELECT query with three variables in the result: resGraph, property, value
     * @param debugName named of the query used for debug log
     * @return map of named graph metadata
     * @throws DatabaseException database error
     */
    protected Model getMetadataFromQuery(String sparqlQuery, String debugName)
            throws DatabaseException {

        Model metadata = new TreeModel();
        GraphQueryResult resultSet = null;
        long startTime = System.currentTimeMillis();
        try {
            resultSet = getConnection().prepareGraphQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate();
            LOG.debug("Query Execution: {} query took {} ms", debugName, System.currentTimeMillis() - startTime);

            while (resultSet.hasNext()) {
                Statement statement = resultSet.next();
                metadata.add(statement);
            }
        } catch (OpenRDFException e) {
            throw new QueryException(e);
        } finally {
            closeResultSetQuietly(resultSet);
        }

        // Add publisher scores
        addPublisherScores(metadata);

        LOG.debug("Query Execution: {} in {} ms", debugName, System.currentTimeMillis() - startTime);
        return metadata;
    }

    /**
     * Retrieve scores of publishers for all publishers occurring in given metadata.
     * @param metadata metadata retrieved for a query
     * @throws DatabaseException database error
     */
    protected void addPublisherScores(Model metadata) throws DatabaseException {
        long startTime = System.currentTimeMillis();

        Set<String> publishers = new HashSet<String>();
        for (Statement statement : metadata.filter(null, PUBLISHED_BY_PROPERTY, null)) {
            if (statement.getObject() instanceof URI) {
                publishers.add(statement.getObject().stringValue());
            }
        }

        Iterable<CharSequence> limitedURIListBuilder = new LimitedURIListBuilder(publishers, MAX_QUERY_LIST_LENGTH);
        for (CharSequence publisherURIList : limitedURIListBuilder) {
            String query = String.format(Locale.ROOT, PUBLISHER_SCORE_QUERY, publisherURIList, maxLimit);
            long queryStartTime = System.currentTimeMillis();
            GraphQueryResult resultSet = null;
            try {
                resultSet = getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
                LOG.debug("Query Execution: addPublisherScores() query took {} ms", System.currentTimeMillis() - queryStartTime);
                while (resultSet.hasNext()) {
                    Statement statement = resultSet.next();
                    if (statement.getObject() instanceof Literal) {
                        metadata.add(statement);
                    }
                }
            } catch (OpenRDFException e) {
                throw new QueryException(e);
            } finally {
                closeResultSetQuietly(resultSet);
            }
        }

        LOG.debug("Query Execution: addPublisherScores() took {} ms", System.currentTimeMillis() - startTime);
    }

    /**
     * Retrieves labels for given resources, converts them to Quads, adds them to addToCollection and returns it.
     * @param resourceURIs URIs of resources to retrieve labels for
     * @param addToCollection collection where retrieved labels are added
     * @return collection containing contents of addToCollection plus new quads for labels
     * @throws DatabaseException database error
     */
    protected Collection<Statement> addLabelsForResources(Collection<String> resourceURIs, Collection<Statement> addToCollection)
            throws DatabaseException {

        long startTime = System.currentTimeMillis();
        Iterable<CharSequence> resourceURIListBuilder = new LimitedURIListBuilder(resourceURIs, MAX_QUERY_LIST_LENGTH);

        TupleQueryResult resultSet = null;
        try {
            for (CharSequence resourceURIList : resourceURIListBuilder) {
                String query = String.format(Locale.ROOT, LABELS_QUERY, resourceURIList, labelPropertiesList,
                        getGraphPrefixFilter("labelGraph"), maxLimit);

                long queryStartTime = System.currentTimeMillis();
                resultSet = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
                LOG.debug("Query Execution: {} query took {} ms",
                        "getLabelsForResources()", System.currentTimeMillis() - queryStartTime);

                while (resultSet.hasNext()) {
                    BindingSet bindingSet = resultSet.next();
                    Statement quad = VALUE_FACTORY.createStatement(
                            (Resource) bindingSet.getValue("r"),
                            (URI) bindingSet.getValue("labelProp"),
                            bindingSet.getValue("label"),
                            (Resource) bindingSet.getValue("labelGraph"));
                    addToCollection.add(quad);
                }

                closeResultSetQuietly(resultSet);
                resultSet = null;
            }
        } catch (OpenRDFException e) {
            throw new QueryException(e);
        } finally {
            closeResultSetQuietly(resultSet);
        }
        LOG.debug("Query Execution: {} in {} ms", "getLabelsForResources()", System.currentTimeMillis() - startTime);
        return addToCollection;
    }

    /**
     * Retrieves owl:sameAs links of the given URI to all resources that are a synonym
     * (i.e. connected by a owl:sameAs path) of the given URI. The result is added to the second argument as
     * triples having the owl:sameAs predicate. The maximum length of the owl:sameAs path is given by
     * {@link #MAX_SAMEAS_PATH_LENGTH} ({@value #MAX_SAMEAS_PATH_LENGTH}).
     * @param uri URI for which we search for owl:sameAs synonyms
     * @param triples the resulting triples are added to this collection as
     * @throws DatabaseException database error
     */
    protected void addSameAsLinksForURI(String uri, Collection<Statement> triples) throws DatabaseException {
        TupleQueryResult resultSet = null;
        try {
            long startTime = System.currentTimeMillis();
            String query = String.format(Locale.ROOT, URI_SYNONYMS_QUERY, uri, MAX_SAMEAS_PATH_LENGTH, maxLimit);
            resultSet = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
            LOG.debug("Query Execution: getURISynonyms() query took {} ms", System.currentTimeMillis() - startTime);
            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();
                Statement triple = VALUE_FACTORY.createStatement(
                        (Resource) bindingSet.getValue("r"),
                        SAME_AS_PROPERTY,
                        (URI) bindingSet.getValue("syn"));
                triples.add(triple);
            }
        } catch (OpenRDFException e) {
            throw new QueryException(e);
        } finally {
            closeResultSetQuietly(resultSet);
        }
    }

    /**
     * Creates a new instance of ConflictResolver using the correct default settings.
     * @param metadata metadata model
     * @param sameAsLinks statements with owl:sameAs as predicate
     * @param preferredURIs URIs preferred as canonical URIs
     * @return a new ConflictResolver instance
     */
    protected ConflictResolver createConflictResolver(
            Model metadata, Iterator<Statement> sameAsLinks, Set<String> preferredURIs) {
        String resultGraphPrefix =
                globalConfig.getResultDataURIPrefix().toString() + ODCSInternal.queryResultGraphUriInfix;

        // Merge user and admin CR policies
        ResolutionStrategy mergedDefaultStrategy = CRUtils.fillResolutionStrategyDefaults(
                conflictResolutionPolicy.getDefaultResolutionStrategy(),
                defaultResolutionPolicy.getDefaultResolutionStrategy());

        Map<URI, ResolutionStrategy> mergedPropertyStrategies = new HashMap<URI, ResolutionStrategy>(
                conflictResolutionPolicy.getPropertyResolutionStrategies());
        for (Entry<URI, ResolutionStrategy> entry : defaultResolutionPolicy.getPropertyResolutionStrategies().entrySet()) {
            ResolutionStrategy mergedStrategy = CRUtils.fillResolutionStrategyDefaults(
                    mergedPropertyStrategies.get(entry.getKey()),
                    entry.getValue());
            mergedPropertyStrategies.put(entry.getKey(), mergedStrategy);
        }

        return ConflictResolverFactory.configureResolver()
                .setResolutionFunctionRegistry(resolutionFunctionRegistry)
                .setDefaultResolutionStrategy(mergedDefaultStrategy)
                .setPropertyResolutionStrategies(mergedPropertyStrategies)
                .setResolvedGraphsURIPrefix(resultGraphPrefix)
                .setMetadata(metadata)
                .setPreferredCanonicalURIs(preferredURIs)
                .addSameAsLinks(sameAsLinks)
                .create();
    }

    /**
     * Returns preferred URIs for the result based on aggregation settings.
     * These include the properties explicitly listed in aggregation settings.
     * @return preferred URIs
     */
    protected Set<String> getSettingsPreferredURIs() {
        Set<String> preferredURIs = new HashSet<String>(conflictResolutionPolicy.getPropertyResolutionStrategies().size());
        for (URI uri : conflictResolutionPolicy.getPropertyResolutionStrategies().keySet()) {
            preferredURIs.add(uri.stringValue());
        }
        return preferredURIs;
    }

    private void closeResultSetQuietly(QueryResult<?> resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (QueryEvaluationException e) {
                // ignore
            }
        }
    }
}
