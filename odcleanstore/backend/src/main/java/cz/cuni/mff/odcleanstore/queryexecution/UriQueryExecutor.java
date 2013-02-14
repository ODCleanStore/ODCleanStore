package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.QueryExecutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
            + "\n   ?resGraph ?p ?o"
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
            // fix of SPARQL compiler error: "sparp_gp_deprecate(): equiv replaces filter but under deprecation"
            + "\n                 FILTER (?o IN (<%1$s>, <%1$s>))"
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
            + "\n   {"
            //+ "\n     {"
            + "\n       ?resGraph <" + ODCS.metadataGraph + "> ?metadataGraph"
            + "\n       GRAPH ?metadataGraph {"
            + "\n         ?resGraph ?p ?o"
            + "\n       }"
            //+ "\n     }"
            //+ "\n     UNION"
            //+ "\n     {"
            //+ "\n       ?resGraph <" + ODCS.publishedBy + "> ?publishedBy."
            //+ "\n       ?publishedBy ?p ?o."
            //+ "\n       FILTER (?p = <" + ODCS.publisherScore + ">)"
            //+ "\n     }"
            + "\n   }"
            + "\n   %4$s"
            + "\n }"
            + "\n LIMIT %5$d";

    /**
     * Creates a new instance of UriQueryExecutor.
     * @param connectionCredentials connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution;
     *        property names must not contain prefixed names
     * @param conflictResolverFactory factory for ConflictResolver
     * @param labelPropertiesList list of label properties formatted as a string for use in a query
     * @param globalConfig global conflict resolution settings
     */
    public UriQueryExecutor(JDBCConnectionCredentials connectionCredentials, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec, ConflictResolverFactory conflictResolverFactory,
            String labelPropertiesList, QueryExecutionConfig globalConfig) {
        super(connectionCredentials, constraints, aggregationSpec, conflictResolverFactory,
                labelPropertiesList, globalConfig);
    }

    /**
     * Executes the URI search query.
     *
     * @param uri searched URI; must be an absolute URI, not a prefixed name
     * @return query result holder
     * @throws QueryExecutionException database error or the query was invalid
     */
    public BasicQueryResult findURI(String uri) throws QueryExecutionException {
        LOG.info("URI query for <{}>", uri);
        long startTime = System.currentTimeMillis();
        checkValidSettings();

        // Check that the URI is valid (must not be empty or null, should match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>' )
        if (uri.length() > MAX_URI_LENGTH) {
            throw new QueryExecutionException(EnumQueryError.QUERY_TOO_LONG, ODCSErrorCodes.QE_INPUT_FORMAT_ERR,
                    "The requested URI is longer than " + MAX_URI_LENGTH + " characters.");
        }
        if (!ODCSUtils.isValidIRI(uri)) {
            throw new QueryExecutionException(EnumQueryError.INVALID_QUERY_FORMAT, ODCSErrorCodes.QE_INPUT_FORMAT_ERR,
                    "The query is not a valid URI.");
        }

        try {
            // Get the quads relevant for the query
            Collection<Quad> quads = getURIOccurrences(uri);
            if (quads.isEmpty()) {
                return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(), uri,
                        System.currentTimeMillis() - startTime);
            }
            quads = addLabels(quads, uri);

            // Apply conflict resolution
            NamedGraphMetadataMap metadata = getMetadata(uri);
            Iterator<Triple> sameAsLinks = getSameAsLinks(uri).iterator();
            Set<String> preferredURIs = getSettingsPreferredURIs();
            preferredURIs.add(uri);
            ConflictResolver conflictResolver =
                    conflictResolverFactory.createResolver(aggregationSpec, metadata, sameAsLinks, preferredURIs);
            Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

            return createResult(resolvedQuads, metadata, uri, System.currentTimeMillis() - startTime);
        } catch (ConflictResolutionException e) {
            throw new QueryExecutionException(
                    EnumQueryError.CONFLICT_RESOLUTION_ERROR,
                    ODCSErrorCodes.QE_CR_ERR,
                    "Internal error during conflict resolution",
                    e);
        } catch (DatabaseException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, ODCSErrorCodes.QE_DATABASE_ERR, "Database error", e);
        } finally {
            closeConnectionQuietly();
        }
    }

    /**
     * Creates an object holding the results of the query.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param query the queried URI
     * @param executionTime query execution time in ms
     * @return query result holder
     */
    private BasicQueryResult createResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            String query,
            long executionTime) {

        LOG.debug("Query Execution: findURI() in {} ms", executionTime);
        // Format and return result
        BasicQueryResult queryResult = new BasicQueryResult(resultQuads, metadata, query, EnumQueryType.URI, constraints,
                aggregationSpec);
        queryResult.setExecutionTime(executionTime);
        return queryResult;
    }

    /**
     * Return a collection of quads relevant for the query (without metadata or any additional quads).
     * @param uri searched URI
     * @return retrieved quads
     * @throws DatabaseException query error
     */
    private Collection<Quad> getURIOccurrences(String uri) throws DatabaseException {
        String query = String.format(Locale.ROOT, URI_OCCURENCES_QUERY, uri, getGraphFilterClause(), maxLimit);
        return getQuadsFromQuery(query, "getURIOccurrences()");
    }

    /**
     * Return quads collection enriched with labels of resources returned by {{@link #getURIOccurrences(String)} as quads.
     * @param quads quads already retrieved for the query
     * @param uri searched URI
     * @return quads parameter with added label quads
     * @throws DatabaseException query error
     */
    private Collection<Quad> addLabels(Collection<Quad> quads, String uri) throws DatabaseException {
        long startTime = System.currentTimeMillis();
        HashSet<String> resources = new HashSet<String>();
        for (Quad quad : quads) {
            Node subject = quad.getSubject();
            if (subject.isURI()) {
                resources.add(subject.getURI());
            } else if (subject.isBlank()) {
                resources.add(ODCSUtils.getVirtuosoURIForBlankNode(subject));
            }

            Node predicate = quad.getPredicate();
            if (predicate.isURI()) {
                resources.add(predicate.getURI());
            } else if (predicate.isBlank()) {
                resources.add(ODCSUtils.getVirtuosoURIForBlankNode(predicate));
            }

            Node object = quad.getObject();
            if (object.isURI()) {
                resources.add(object.getURI());
            } else if (object.isBlank()) {
                resources.add(ODCSUtils.getVirtuosoURIForBlankNode(object));
            }
        }
        resources.remove(uri);
        resources.remove(OWL.sameAs);
        LOG.debug("Query Execution: addLabels() for {} resources (found in {} ms)",
                resources.size(), System.currentTimeMillis() - startTime);

        return addLabelsForResources(resources, quads);
    }

    /**
     * Return metadata for named graphs containing quads returned in the result.
     * @param uri searched URI
     * @return metadata of result named graphs
     * @throws DatabaseException query error
     */
    private NamedGraphMetadataMap getMetadata(String uri) throws DatabaseException {
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, getGraphFilterClause(),
                labelPropertiesList, getGraphPrefixFilter("resGraph"), maxLimit);
        return getMetadataFromQuery(query, "getMetadata()");
    }

    /**
     * Returns owl:sameAs links relevant for conflict resolution for this query.
     * Returns only links for the searched URI and properties explicitly listed in aggregation settings;
     * other links (e.g. between subjects/objects in the result) are resolved by Virtuoso.
     * @see #URI_OCCURENCES_QUERY
     * @param uri searched URI
     * @return collection of relevant owl:sameAs links
     * @throws DatabaseException query error
     */
    private Collection<Triple> getSameAsLinks(String uri) throws DatabaseException {
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
