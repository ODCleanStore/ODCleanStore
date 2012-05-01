package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.queryexecution.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Executes the URI search query.
 * Triples that contain the given URI as their subject or object are returned.
 *
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
/*package*/class UriQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(UriQueryExecutor.class);

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
    private static final String INSERTED_AT_FILTER_CLAUSE = " OPTIONAL { ?graph <" + W3P.insertedAt + "> ?_insertedAt }"
            + " FILTER(?_insertedAt >= \"%s\"^^<" + XMLSchema.dateTimeType + ">)";

    /**
     * SPARQL snippet restricting a variable to start with the given string.
     * Must be formatted with a string argument.
     */
    private static final String PREFIX_FILTER_CLAUSE = " FILTER regex(?%s, \"^%s\")";

    /**
     * SPARQL query that gets the main result quads.
     * Use of UNION instead of a more complex filter is to make owl:sameAs inference in Virtuoso work.
     * The subquery is necessary to make Virtuoso translate subjects/objects to a single owl:sameAs equivalent.
     * This way we don't need to obtain sameAs links (passed to ConflictResolverSpec) from the database explicitly.
     *
     * The query must be formatted with three arguments: URI, graph filter clause, limit
     *
     * TODO: it seemed to perform faster with OPTIONAL clauses for time and score - check it
     * TODO: explore using REDUCED instead of DISTINCT
     */
    private static final String URI_OCCURENCES_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?graph ?s ?p ?o"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?s = <%1$s>)"
            + "\n           FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n         %2$s"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?o = <%1$s>)"
            + "\n           FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n         %2$s"
            + "\n       }"
            + "\n     }"
            + "\n     LIMIT %3$d"
            + "\n   }"
            + "\n }";

    /**
     * SPARQL query that gets metadata for named graphs containing result quads.
     * Source is the only required value, others can be null.
     * For the reason why UNIONs and subqueries are used, see {@link #URI_OCCURENCES_QUERY}.
     *
     * OPTIONAL clauses for fetching ?graph properties are necessary (probably due to Virtuoso inference processing).
     *
     * Must be formatted with arguments: URI, graph filter clause, label properties, resGraph prefix filter, limit
     *
     * TODO: omit metadata for additional labels?
     * TODO: reuse uri query and label query?
     */
    private static final String METADATA_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT"
            + "\n   ?resGraph ?source ?score ?insertedAt ?insertedBy ?license ?publishedBy ?publisherScore"
            + "\n WHERE {"
            + "\n   {"
            + "\n     {"
            + "\n       SELECT DISTINCT ?graph as ?resGraph"
            + "\n       WHERE {"
            + "\n         {"
            + "\n           GRAPH ?graph {"
            + "\n              ?s ?p ?o."
            + "\n              FILTER (?s = <%1$s>)"
            + "\n              FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n           }"
            + "\n           %2$s"
            + "\n         }"
            + "\n         UNION"
            + "\n         {"
            + "\n           GRAPH ?graph {"
            + "\n              ?s ?p ?o."
            + "\n              FILTER (?o = <%1$s>)"
            + "\n              FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n           }"
            + "\n           %2$s"
            + "\n         }"
            + "\n       }"
            + "\n       LIMIT %5$d"
            + "\n     }"
            + "\n     UNION"
            + "\n     {"
            + "\n       SELECT DISTINCT ?resGraph"
            + "\n       WHERE {"
            + "\n         {"
            + "\n           SELECT DISTINCT ?r"
            + "\n           WHERE {"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?s ?p ?r."
            + "\n                 FILTER (?s = <%1$s>)"
            + "\n               }"
            + "\n               %2$s"
            + "\n               FILTER (!isLITERAL(?r) && ?p != <" + OWL.sameAs + ">)"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?s ?r ?o."
            + "\n                 FILTER (?s = <%1$s>)"
            + "\n               }"
            + "\n               %2$s"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?r ?p ?o."
            + "\n                 FILTER (?o = <%1$s>)"
            + "\n               }"
            + "\n               %2$s"
            + "\n               FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?s ?r ?o."
            + "\n                 FILTER (?o = <%1$s>)"
            + "\n               }"
            + "\n               %2$s"
            + "\n             }"
            + "\n             FILTER(?r != <%1$s>)"
            + "\n           }"
            + "\n           LIMIT %5$d"
            + "\n         }"
            + "\n         GRAPH ?resGraph {"
            + "\n           ?r ?labelProp ?label"
            + "\n         }"
            + "\n         FILTER (?labelProp IN (%3$s))"
            + "\n       }"
            + "\n       LIMIT %5$d"
            + "\n     }"
            + "\n   }"
            + "\n   OPTIONAL { ?resGraph <" + W3P.source + "> ?source }"
            + "\n   OPTIONAL { ?resGraph <" + ODCS.score + "> ?score }"
            + "\n   OPTIONAL { ?resGraph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n   OPTIONAL { ?resGraph <" + W3P.insertedBy + "> ?insertedBy }"
            + "\n   OPTIONAL { ?resGraph <" + DC.license + "> ?license }"
            + "\n   OPTIONAL { ?resGraph <" + W3P.publishedBy + "> ?publishedBy }"
            + "\n   OPTIONAL { ?resGraph <" + W3P.publishedBy + "> ?publishedBy. "
            + "\n     ?publishedBy <" + ODCS.publisherScore + "> ?publisherScore }"
            + "\n   %4$s"
            + "\n   FILTER (bound(?source))"
            + "\n }"
            + "\n LIMIT %5$d";

    /**
     * SPARQL query for retrieving labels of resources contained in the result, except for the searched URI
     * (we get that by {@link #URI_OCCURENCES_QUERY}).
     *
     * For the reason why UNIONs and subqueries are used, see {@link #URI_OCCURENCES_QUERY}.
     *
     * Must be formatted with arguments: URI, graph filter clause, label properties, ?labelGraph prefix filter, limit.
     *
     * @see QueryExecutorBase#LABEL_PROPERTIES
     */
    private static final String LABELS_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?labelGraph ?r ?labelProp ?label WHERE {{"
            + "\n SELECT DISTINCT ?labelGraph ?r ?labelProp ?label"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?r"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?r."
            + "\n           FILTER (?s = <%1$s>)"
            + "\n         }"
            + "\n         %2$s"
            + "\n         FILTER (!isLITERAL(?r) && ?p != <" + OWL.sameAs + ">)"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           FILTER (?s = <%1$s>)"
            + "\n         }"
            + "\n         %2$s"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?r ?p ?o."
            + "\n           FILTER (?o = <%1$s>)"
            + "\n         }"
            + "\n         %2$s"
            + "\n         FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           FILTER (?o = <%1$s>)"
            + "\n         }"
            + "\n         %2$s"
            + "\n       }"
            + "\n       FILTER(?r != <%1$s>)"
            + "\n     }"
            + "\n     LIMIT %5$d"
            + "\n   }"
            + "\n   GRAPH ?labelGraph {"
            + "\n     ?r ?labelProp ?label"
            + "\n   }"
            + "\n   FILTER (?labelProp IN (%3$s))"
            + "\n   %4$s"
            + "\n }"
            + "\n LIMIT %5$d"
            + "\n }}";

    /**
     * Cached graph filter SPARQL snippet.
     * Depends only on settings immutable during the instance lifetime and thus can be cached.
     */
    private CharSequence graphFilterClause;

    /**
     * Database connection.
     */
    private VirtuosoConnectionWrapper connection;

    /**
     * Creates a new instance of UriQueryExecutor.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     */
    public UriQueryExecutor(SparqlEndpoint sparqlEndpoint, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        super(sparqlEndpoint, constraints, aggregationSpec);
    }

    /**
     * Executes the URI search query.
     *
     * @param uri searched URI
     * @return query result holder
     * @throws URISyntaxException the given URI is not a valid URI
     * @throws ODCleanStoreException database error
     */
    public QueryResult findURI(String uri) throws ODCleanStoreException, URISyntaxException {
        LOG.info("URI query for <{}>", uri);
        long startTime = System.currentTimeMillis();

        // Check that the URI is valid (must not be empty or null, should match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>' )
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            throw e; // rethrow
        }

        try {
            // Get the quads relevant for the query
            Collection<Quad> quads = getURIOccurrences(uri);
            if (quads.isEmpty()) {
                return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(),
                        System.currentTimeMillis() - startTime);
            }
            quads.addAll(getLabels(uri));

            // Gather all settings for Conflict Resolution
            ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
            crSpec.setPreferredURIs(Collections.singleton(uri));
            // ... no need for sameAs links - see URI_OCCURENCES_QUERY
            crSpec.setSameAsLinks(Collections.<Triple>emptySet().iterator());
            NamedGraphMetadataMap metadata = getMetadata(uri);
            crSpec.setNamedGraphMetadata(metadata);

            // Apply conflict resolution
            ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
            Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

            return createResult(resolvedQuads, metadata, System.currentTimeMillis() - startTime);
        } finally {
            closeConnection();
        }
    }

    /**
     * Returns a database connection.
     * The connection is shared within this instance until it is closed.
     * @return database connection
     * @throws ConnectionException database connection error
     */
    private VirtuosoConnectionWrapper getConnection() throws ConnectionException {
        if (connection == null) {
            connection = VirtuosoConnectionWrapper.createConnection(sparqlEndpoint);
        }
        return connection;
    }

    /**
     * Closes an opened database connection, if any.
     * @throws ConnectionException database connection error
     */
    private void closeConnection() throws ConnectionException {
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
    private CharSequence getGraphFilterClause() {
        if (graphFilterClause == null) {
            graphFilterClause = buildGraphFilterClause(constraints);
        }
        return graphFilterClause;
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

    /**
     * Returns a SPARQL snippet restricting a named graph URI referenced by the given variable to GRAPH_PREFIX_FILTER.
     * Returns an empty string if GRAPH_PREFIX_FILTER is null.
     * @see #GRAPH_PREFIX_FILTER
     * @param graphVariable SPARQL variable name
     * @return SPARQL query snippet
     */
    private static String getGraphPrefixFilter(String graphVariable) {
        if (GRAPH_PREFIX_FILTER == null) {
            return "";
        } else {
            return String.format(Locale.ROOT, PREFIX_FILTER_CLAUSE, graphVariable, GRAPH_PREFIX_FILTER);
        }
    }

    /**
     * Creates an object holding the results of the query.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param executionTime query execution time in ms
     * @return query result holder
     */
    private QueryResult createResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            long executionTime) {

        LOG.debug("Query Execution: findURI() in {} ms", executionTime);
        // Format and return result
        QueryResult queryResult = new QueryResult(resultQuads, metadata, EnumQueryType.URI, constraints,
                aggregationSpec);
        queryResult.setExecutionTime(executionTime);
        return queryResult;
    }

    /**
     * Return a collection of quads relevant for the query (without metadata or any additional quads).
     * @param uri searched URI
     * @return retrieved quads
     * @throws ODCleanStoreException query error
     */
    private Collection<Quad> getURIOccurrences(String uri) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();

        String query = String.format(URI_OCCURENCES_QUERY, uri, getGraphFilterClause(), MAX_LIMIT);
        WrappedResultSet resultSet = getConnection().executeSelect(query);
        LOG.debug("Query Execution: getURIOccurences() query took {} ms", System.currentTimeMillis() - startTime);

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

            LOG.debug("Query Execution: getURIOccurrences() in {} ms", System.currentTimeMillis() - startTime);
            return quads;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }

    /**
     * Return labels of resources returned by {{@link #getURIOccurrences(String)}} as quads.
     * @param uri searched URI
     * @return labels as quads
     * @throws ODCleanStoreException query error
     */
    private Collection<Quad> getLabels(String uri) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();

        String query = String.format(Locale.ROOT, LABELS_QUERY, uri, getGraphFilterClause(), LABEL_PROPERTIES_LIST,
                getGraphPrefixFilter("labelGraph"), MAX_LIMIT);
        WrappedResultSet resultSet = getConnection().executeSelect(query);
        LOG.debug("Query Execution: getLabels() query took {} ms", System.currentTimeMillis() - startTime);

        try {
            QuadCollection quads = new QuadCollection();
            while (resultSet.next()) {
                Quad quad = new Quad(
                        resultSet.getNode("labelGraph"),
                        resultSet.getNode("r"),
                        resultSet.getNode("labelProp"),
                        resultSet.getNode("label"));
                quads.add(quad);
            }

            LOG.debug("Query Execution: getLabels() in {} ms", System.currentTimeMillis() - startTime);
            return quads;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }

    /**
     * Return metadata for named graphs containing quads returned in the result.
     * @param uri searched URI
     * @return metadata of result named graphs
     * @throws ODCleanStoreException query error
     */
    private NamedGraphMetadataMap getMetadata(String uri)
            throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        // Execute the query
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, getGraphFilterClause(),
                LABEL_PROPERTIES_LIST, getGraphPrefixFilter("resGraph"), MAX_LIMIT);

        WrappedResultSet resultSet = getConnection().executeSelect(query);
        LOG.debug("Query Execution: getMetadata() query took {} ms", System.currentTimeMillis() - startTime);

        // Build the result
        try {
            NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();
            while (resultSet.next()) {
                NamedGraphMetadata graphMetadata = new NamedGraphMetadata(resultSet.getString("resGraph"));

                String source = resultSet.getString("source");
                graphMetadata.getSource(source);

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

                metadata.addMetadata(graphMetadata);
            }
            LOG.debug("Query Execution: getMetadata() in {} ms", System.currentTimeMillis() - startTime);
            return metadata;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }
}
