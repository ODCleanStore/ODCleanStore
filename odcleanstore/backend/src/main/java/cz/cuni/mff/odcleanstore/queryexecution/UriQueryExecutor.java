package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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

    /** Maximum allowed length of the query. */
    public static final int MAX_URI_LENGTH = 1024;

    /**
     * SPARQL query that gets the main result quads.
     * Use of UNION instead of a more complex filter is to make owl:sameAs inference in Virtuoso work.
     * The subquery is necessary to make Virtuoso translate subjects/objects to a single owl:sameAs equivalent.
     * This way we don't need to obtain owl:sameAs links for subjects/objects (passed to ConflictResolverSpec) from
     * the database explicitly.
     *
     * The query must be formatted with these arguments: (1) URI, (2) graph filter clause, (3) limit
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
     * Must be formatted with arguments: (1) URI, (2) graph filter clause, (3) label properties, (4) resGraph prefix
     * filter, (5) limit
     *
     * TODO: omit metadata for additional labels?
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
     * Must be formatted with arguments: (1) URI, (2) graph filter clause, (3) label properties, (4) ?labelGraph prefix
     * filter, (5) limit.
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
     * @throws ODCleanStoreException database error or the query was invalid
     */
    public QueryResult findURI(String uri) throws ODCleanStoreException {
        LOG.info("URI query for <{}>", uri);
        long startTime = System.currentTimeMillis();
        checkValidSettings();

        // Check that the URI is valid (must not be empty or null, should match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>' )
        if (uri.length() > MAX_URI_LENGTH) {
            throw new QueryException("The requested URI is longer than " + MAX_URI_LENGTH + " characters.");
        }
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            throw new QueryFormatException(e); // rethrow
        }

        try {
            // Get the quads relevant for the query
            Collection<Quad> quads = getURIOccurrences(uri);
            if (quads.isEmpty()) {
                return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(), uri,
                        System.currentTimeMillis() - startTime);
            }
            quads.addAll(getLabels(uri));

            // Gather all settings for Conflict Resolution
            ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
            crSpec.setPreferredURIs(getPreferredURIs(uri));
            crSpec.setSameAsLinks(getSameAsLinks(uri).iterator());
            NamedGraphMetadataMap metadata = getMetadata(uri);
            crSpec.setNamedGraphMetadata(metadata);

            // Apply conflict resolution
            ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
            Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

            return createResult(resolvedQuads, metadata, uri, System.currentTimeMillis() - startTime);
        } finally {
            closeConnection();
        }
    }

    /**
     * Returns preferred URIs for the result.
     * These include the searched URI and properties explicitly listed in aggregation settings.
     * @param uri searched URI
     * @return preferred URIs
     */
    private Set<String> getPreferredURIs(String uri) {
        Set<String> aggregationProperties = aggregationSpec.getPropertyAggregations() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyAggregations().keySet();
        Set<String> multivalueProperties = aggregationSpec.getPropertyMultivalue() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyMultivalue().keySet();
        if (aggregationProperties.isEmpty() && multivalueProperties.isEmpty()) {
            return Collections.singleton(uri);
        }
        Set<String> preferredURIs = new HashSet<String>(aggregationProperties.size() + multivalueProperties.size() + 1);
        preferredURIs.add(uri);
        preferredURIs.addAll(aggregationProperties);
        preferredURIs.addAll(multivalueProperties);
        return preferredURIs;
    }

    /**
     * Creates an object holding the results of the query.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param query the queried URI
     * @param executionTime query execution time in ms
     * @return query result holder
     */
    private QueryResult createResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            String query,
            long executionTime) {

        LOG.debug("Query Execution: findURI() in {} ms", executionTime);
        // Format and return result
        QueryResult queryResult = new QueryResult(resultQuads, metadata, query, EnumQueryType.URI, constraints,
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
        String query = String.format(URI_OCCURENCES_QUERY, uri, getGraphFilterClause(), MAX_LIMIT);
        return getQuadsFromQuery(query, "getURIOccurrences()");
    }

    /**
     * Return labels of resources returned by {{@link #getURIOccurrences(String)} as quads.
     * @param uri searched URI
     * @return labels as quads
     * @throws ODCleanStoreException query error
     */
    private Collection<Quad> getLabels(String uri) throws ODCleanStoreException {
        String query = String.format(Locale.ROOT, LABELS_QUERY, uri, getGraphFilterClause(), LABEL_PROPERTIES_LIST,
                getGraphPrefixFilter("labelGraph"), MAX_LIMIT);
        return getQuadsFromQuery(query, "getLabels()");
    }

    /**
     * Return metadata for named graphs containing quads returned in the result.
     * @param uri searched URI
     * @return metadata of result named graphs
     * @throws ODCleanStoreException query error
     */
    private NamedGraphMetadataMap getMetadata(String uri) throws ODCleanStoreException {
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, getGraphFilterClause(),
                LABEL_PROPERTIES_LIST, getGraphPrefixFilter("resGraph"), MAX_LIMIT);
        return getMetadataFromQuery(query, "getMetadata()");
    }

    /**
     * Returns owl:sameAs links relevant for conflict resolution for this query.
     * Returns only links for the searched URI and properties explicitly listed in aggregation settings;
     * other links (e.g. between subjects/objects in the result) are resolved by Virtuoso.
     * @see #URI_OCCURENCES_QUERY
     * @param uri searched URI
     * @return collection of relevant owl:sameAs links
     * @throws ODCleanStoreException query error
     */
    private Collection<Triple> getSameAsLinks(String uri) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();
        Collection<Triple> sameAsTriples = new ArrayList<Triple>();
        addSameAsLinksForURI(uri, sameAsTriples);
        assert aggregationSpec.getPropertyAggregations() != null;
        for (String property : aggregationSpec.getPropertyAggregations().keySet()) {
            addSameAsLinksForURI(property, sameAsTriples);
        }
        assert aggregationSpec.getPropertyMultivalue() != null;
        for (String property : aggregationSpec.getPropertyMultivalue().keySet()) {
            addSameAsLinksForURI(property, sameAsTriples);
        }
        LOG.debug("Query Execution: getSameAsLinks() in {} ms ({} links)",
                System.currentTimeMillis() - startTime, sameAsTriples.size());
        return sameAsTriples;
    }
}
