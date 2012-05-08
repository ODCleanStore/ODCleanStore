package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.RDFS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

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

    /**
     * (Debug) Only named graph having URI starting with this prefix can be included in query result.
     * If the value is null, there is now restriction on named graph URIs.
     * This constant is only for debugging purposes and should be null in production environment.
     * TODO: set to null
     */
    private static final String GRAPH_PREFIX_FILTER = null; //"http://odcs.mff.cuni.cz/namedGraph/qe-test/";

    /**
     * Maximum number of triples returned by each database query (the overall result size may be larger).
     * TODO: get from global configuration.
     */
    protected static final long MAX_LIMIT = 500;

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
     * Prefix of named graphs where the resulting triples are placed.
     * TODO: get from global configuration.
     */
    protected static final String RESULT_GRAPH_PREFIX = "http://odcs.mff.cuni.cz/results/";

    /**
     * A {@link Node} representing the owl:sameAs predicate.
     */
    protected static final Node SAME_AS_PROPERTY = Node.createURI(OWL.sameAs);

    /** Properties designating a human-readable label. */
    protected static final String[] LABEL_PROPERTIES = new String[] { RDFS.label };

    /** List of {@link #LABEL_PROPERTIES} formatted to a string for use in a SPARQL query. */
    protected static final String LABEL_PROPERTIES_LIST;

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
            + " { ?graph <" + W3P.insertedAt + "> ?_insertedAt }"
            + " FILTER(?_insertedAt >= \"%s\"^^<" + XMLSchema.dateTimeType + ">)";

    /**
     * SPARQL snippet restricting a variable to start with the given string.
     * Must be formatted with a string argument.
     */
    private static final String PREFIX_FILTER_CLAUSE = " FILTER regex(?%s, \"^%s\")";

    /**
     * SPARQL query for retrieving all synonyms (i.e. resources connected by an owl:sameAs path) of a given URI.
     * Must be formatted with arguments: (1) URI, (2) maximum length of owl:sameAs path considered, (3) limit.
     * @see #MAX_SAMEAS_PATH_LENGTH
     */
    private static final String URI_SYNONYMS_QUERY = "SPARQL"
                + "\n SELECT ?r ?syn"
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

    static {
        assert (LABEL_PROPERTIES.length > 0);
        StringBuilder sb = new StringBuilder();
        for (String property : LABEL_PROPERTIES) {
            sb.append('<');
            sb.append(property);
            sb.append(">, ");
        }
        LABEL_PROPERTIES_LIST = sb.substring(0, sb.length() - 2);
    }

    /**
     * Returns a SPARQL snippet restricting a named graph URI referenced by the given variable to GRAPH_PREFIX_FILTER.
     * Returns an empty string if GRAPH_PREFIX_FILTER is null.
     * @see #GRAPH_PREFIX_FILTER
     * @param graphVariable SPARQL variable name
     * @return SPARQL query snippet
     */
    protected static String getGraphPrefixFilter(String graphVariable) {
        if (GRAPH_PREFIX_FILTER == null) {
            return "";
        } else {
            return String.format(Locale.ROOT, PREFIX_FILTER_CLAUSE, graphVariable, GRAPH_PREFIX_FILTER);
        }
    }

    /**
     * @see {@link #getGraphFilterClause()}
     * @param constraints constraints on triples returned in the result
     * @return SPARQL query snippet
     */
    private static CharSequence buildGraphFilterClause(QueryConstraintSpec constraints) {
        if (constraints.getMinScore() == null && constraints.getOldestTime() == null && GRAPH_PREFIX_FILTER == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (constraints.getMinScore() != null) {
            sb.append(String.format(Locale.ROOT, SCORE_FILTER_CLAUSE, constraints.getMinScore()));
        }
        if (constraints.getOldestTime() != null) {
            java.sql.Timestamp oldestTime = new Timestamp(constraints.getOldestTime().getTime());
            sb.append(String.format(Locale.ROOT, INSERTED_AT_FILTER_CLAUSE, oldestTime.toString()));
        }
        if (GRAPH_PREFIX_FILTER != null) {
            sb.append(getGraphPrefixFilter("graph"));
        }
        return sb;
    }

    /** Connection settings for the SPARQL endpoint that will be queried. */
    protected final SparqlEndpoint sparqlEndpoint;

    /** Constraints on triples returned in the result. */
    protected final QueryConstraintSpec constraints;

    /** Database connection. */
    private VirtuosoConnectionWrapper connection;

    /**
     * Cached graph filter SPARQL snippet.
     * Depends only on settings immutable during the instance lifetime and thus can be cached.
     */
    private CharSequence graphFilterClause;

    /** Aggregation settings for conflict resolution. */
    protected final AggregationSpec aggregationSpec;

    /**
     * Creates a new instance of QueryExecutorBase.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     */
    protected QueryExecutorBase(SparqlEndpoint sparqlEndpoint, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.constraints = constraints;
        this.aggregationSpec = aggregationSpec;
    }

    /**
     * Returns a database connection.
     * The connection is shared within this instance until it is closed.
     * @return database connection
     * @throws ConnectionException database connection error
     */
    protected VirtuosoConnectionWrapper getConnection() throws ConnectionException {
        if (connection == null) {
            connection = VirtuosoConnectionWrapper.createConnection(sparqlEndpoint);
        }
        return connection;
    }

    /**
     * Closes an opened database connection, if any.
     * @throws ConnectionException database connection error
     */
    protected void closeConnection() throws ConnectionException {
        if (connection != null) {
            connection.close();
            connection = null;
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
     * @throws QueryFormatException aggregation settings or query constraints are invalid
     */
    protected void checkValidSettings() throws QueryFormatException {
        // Check that settings contain valid URIs
        for (String property : aggregationSpec.getPropertyAggregations().keySet()) {
            try {
                new URI(property);
            } catch (URISyntaxException e) {
                throw new QueryFormatException("'" + property + "' is not a valid URI.", e);
            }
        }
        for (String property : aggregationSpec.getPropertyMultivalue().keySet()) {
            try {
                new URI(property);
            } catch (URISyntaxException e) {
                throw new QueryFormatException("'" + property + "' is not a valid URI.", e);
            }
        }

        // Check that the size of settings is reasonable - the query size may depend on it
        int settingsPropertyCount = aggregationSpec.getPropertyAggregations().size()
                + aggregationSpec.getPropertyMultivalue().size();
        if (settingsPropertyCount > MAX_PROPERTY_SETTINGS_SIZE) {
            throw new QueryFormatException("Too many explicit property settings.");
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
     * @throws ODCleanStoreException database query error
     */
    protected Collection<Quad> getQuadsFromQuery(String sparqlQuery, String debugName) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();
        WrappedResultSet resultSet = getConnection().executeSelect(sparqlQuery);
        LOG.debug("Query Execution: {} query took {} ms", debugName, System.currentTimeMillis() - startTime);

        try {
            QuadCollection quads = new QuadCollection();
            while (resultSet.next()) {
                // CHECKSTYLE:OFF
                Quad quad = new Quad(
                        resultSet.getNode(1),
                        resultSet.getNode(2),
                        resultSet.getNode(3),
                        resultSet.getNode(4));
                quads.add(quad);
                // CHECKSTYLE:ON
            }

            LOG.debug("Query Execution: {} in {} ms", debugName, System.currentTimeMillis() - startTime);
            return quads;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }

    /**
     * Extract named graph metadata from the result of the given SPARQL SELECT query.
     * The query must contain these variables in the result: resGraph, source, score, insertedAt, insertedBy, license,
     * publishedBy, publisherScore. Values of these variables may be null.
     * @param sparqlQuery a SPARQL SELECT query that has these variables in the result: resGraph, source, score,
     *        insertedAt, insertedBy, license, publishedBy, publisherScore
     * @param debugName named of the query used for debug log
     * @return map of named graph metadata
     * @throws ODCleanStoreException database query error
     */
    protected NamedGraphMetadataMap getMetadataFromQuery(String sparqlQuery, String debugName)
            throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();
        WrappedResultSet resultSet = getConnection().executeSelect(sparqlQuery);
        LOG.debug("Query Execution: {} query took {} ms", debugName, System.currentTimeMillis() - startTime);

        // Build the result
        try {
            NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();
            while (resultSet.next()) {
                NamedGraphMetadata graphMetadata = new NamedGraphMetadata(resultSet.getString("resGraph"));

                try {
                    String source = resultSet.getString("source");
                    graphMetadata.setSource(source);

                    Double score = resultSet.getDouble("score");
                    graphMetadata.setScore(score);

                    Date insertedAt = resultSet.getJavaDate("insertedAt");
                    graphMetadata.setInsertedAt(insertedAt);

                    String insertedBy = resultSet.getString("insertedBy");
                    graphMetadata.setInsertedBy(insertedBy);

                    String license = resultSet.getString("license");
                    graphMetadata.setLicence(license);

                    String publishedBy = resultSet.getString("publishedBy");
                    graphMetadata.setPublisher(publishedBy);

                    Double publisherScore = resultSet.getDouble("publisherScore");
                    graphMetadata.setPublisherScore(publisherScore);
                } catch (SQLException e) {
                    LOG.warn("Query Execution: invalid metadata for graph {}", graphMetadata.getNamedGraphURI());
                }

                metadata.addMetadata(graphMetadata);
            }
            LOG.debug("Query Execution: {} in {} ms", debugName, System.currentTimeMillis() - startTime);
            return metadata;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }

    /**
     * Retrieves owl:sameAs links of the given URI to all resources that are a synonym
     * (i.e. connected by a owl:sameAs path) of the given URI. The result is added to the second argument as
     * triples having the owl:sameAs predicate.  The maximum length of the owl:sameAs path is given by
     *  {@link #MAX_SAMEAS_PATH_LENGTH} ({@value #MAX_SAMEAS_PATH_LENGTH}).
     * @param uri URI for which we search for owl:sameAs synonyms
     * @param triples the resulting triples are added to this collection as
     * @throws QueryException database query error
     * @throws ConnectionException database connection error
     */
    protected void addSameAsLinksForURI(String uri, Collection<Triple> triples)
            throws QueryException, ConnectionException {

        long startTime = System.currentTimeMillis();
        String query = String.format(URI_SYNONYMS_QUERY, uri, MAX_SAMEAS_PATH_LENGTH, MAX_LIMIT);
        WrappedResultSet resultSet = getConnection().executeSelect(query);
        LOG.debug("Query Execution: getURISynonyms() query took {} ms", System.currentTimeMillis() - startTime);
        try {
            while (resultSet.next()) {
                Triple triple = Triple.create(resultSet.getNode(1), SAME_AS_PROPERTY, resultSet.getNode(2));
                triples.add(triple);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }
}
