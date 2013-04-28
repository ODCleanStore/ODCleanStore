package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.configuration.QueryExecutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.EmptyMetadataModel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Executes the named graph query.
 * All triples from the given named graph are returned.
 *
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
/*package*/class NamedGraphQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(NamedGraphQueryExecutor.class);

    /**
     * SPARQL query that gets the main result quads.
     * The subquery is necessary to make Virtuoso translate subjects/objects to a single owl:sameAs equivalent.
     * This way we don't need to obtain owl:sameAs links for subjects/objects (passed to ConflictResolverSpec) from
     * the database explicitly.
     *
     * The query must be formatted with these arguments: (1) URI, (2) graph filter clause, (3) limit
     */
    private static final String NAMED_GRAPH_QUERY =
            "DEFINE input:same-as \"yes\""
            + "\n SELECT ?graph ?s ?p ?o"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n     WHERE {"
            + "\n       GRAPH ?graph {"
            + "\n         ?s ?p ?o."
            + "\n         FILTER (?graph = <%1$s>)"
            + "\n         FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n       }"
            + "\n       %2$s"
            + "\n     }"
            + "\n     LIMIT %3$d"
            + "\n   }"
            + "\n }";

    /**
     * SPARQL query that gets metadata for named graphs containing result quads.
     * Source is the only required value, others can be null.
     * For the reason why UNIONs and subqueries are used, see {@link #NAMED_GRAPH_QUERY}.
     *
     * OPTIONAL clauses for fetching ?graph properties are necessary (probably due to Virtuoso inference processing).
     *
     * Must be formatted with arguments: (1) URI, (2) resGraph prefix filter, (3) limit
     */
    private static final String METADATA_QUERY =
            //+ "\n DEFINE input:same-as \"yes\""
            "CONSTRUCT "
            + "\n   { <%1$s> ?p ?o }"
            + "\n WHERE {"
            //+ "\n   {"
            + "\n     <%1$s> <" + ODCS.metadataGraph + "> ?metadataGraph"
            + "\n     GRAPH ?metadataGraph {"
            + "\n       <%1$s> ?p ?o"
            + "\n     }"
            //+ "\n   }"
            //+ "\n   UNION"
            //+ "\n   {"
            //+ "\n     <%1$s> <" + ODCS.publishedBy + "> ?publishedBy."
            //+ "\n     ?publishedBy ?p ?o."
            //+ "\n     FILTER (?p = <" + ODCS.publisherScore + ">)"
            //+ "\n   }"
            + "\n   %2$s"
            //+ "\n   FILTER (bound(?source))"
            + "\n }"
            + "\n LIMIT %3$d";

    /**
     * Creates a new instance.
     * @param connectionCredentials connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param conflictResolutionPolicy conflict resolution strategies for conflict resolution;
     *        property names must not contain prefixed names
     * @param labelPropertiesList list of label properties formatted as a string for use in a query
     * @param globalConfig global conflict resolution settings
     */
    public NamedGraphQueryExecutor(JDBCConnectionCredentials connectionCredentials, QueryConstraintSpec constraints,
            ConflictResolutionPolicy conflictResolutionPolicy, String labelPropertiesList,
            QueryExecutionConfig globalConfig) {
        super(connectionCredentials, constraints, conflictResolutionPolicy, labelPropertiesList, globalConfig);
    }

    /**
     * Executes the named graph search query.
     *
     * @param uri URI of the requested named graph; must be an absolute URI, not a prefixed name
     * @return query result holder
     * @throws QueryExecutionException database error or the query was invalid
     */
    public BasicQueryResult getNamedGraph(String uri) throws QueryExecutionException {
        LOG.info("Named graph query for <{}>", uri);
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
            Collection<Statement> quads = getNamedGraphTriples(uri);
            if (quads.isEmpty()) {
                return createResult(Collections.<ResolvedStatement>emptyList(), new EmptyMetadataModel(), uri,
                        System.currentTimeMillis() - startTime);
            }

            // Apply conflict resolution
            Model metadata = getMetadata(uri);
            Iterator<Statement> sameAsLinks = getSameAsLinks().iterator();
            Set<String> preferredURIs = getSettingsPreferredURIs();
            ConflictResolver conflictResolver = createConflictResolver(
                    conflictResolutionPolicy, metadata, sameAsLinks, preferredURIs);
            Collection<ResolvedStatement> resolvedQuads = conflictResolver.resolveConflicts(quads);

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
            Collection<ResolvedStatement> resultQuads,
            Model metadata,
            String query,
            long executionTime) {

        LOG.debug("Query Execution: getNamedGraph() in {} ms", executionTime);
        // Format and return result
        BasicQueryResult queryResult = new BasicQueryResult(resultQuads, metadata, query, EnumQueryType.NAMED_GRAPH,
                constraints, conflictResolutionPolicy);
        queryResult.setExecutionTime(executionTime);
        return queryResult;
    }

    /**
     * Return a collection of quads relevant for the query (without metadata or any additional quads).
     * @param namedGraphURI URI of the requested named graph
     * @return retrieved quads
     * @throws DatabaseException query error
     */
    private Collection<Statement> getNamedGraphTriples(String namedGraphURI) throws DatabaseException {
        String query = String.format(Locale.ROOT, NAMED_GRAPH_QUERY, namedGraphURI, getGraphFilterClause(), maxLimit);
        return getQuadsFromQuery(query, "getNamedGraphTriples()");
    }

    /**
     * Return metadata for named graphs containing quads returned in the result.
     * @param namedGraphURI URI of the requested named graph
     * @return metadata of result named graphs
     * @throws DatabaseException query error
     */
    private Model getMetadata(String namedGraphURI) throws DatabaseException {
        String query = String.format(Locale.ROOT, METADATA_QUERY, namedGraphURI,
                getGraphPrefixFilter("resGraph"), maxLimit);
        return getMetadataFromQuery(query, "getMetadata()");
    }

    /**
     * Returns owl:sameAs links relevant for conflict resolution for this query.
     * Returns only links for properties explicitly listed in aggregation settings;
     * other links (e.g. between subjects/objects in the result) are resolved by Virtuoso.
     * @see #NAMED_GRAPH_QUERY
     * @return collection of relevant owl:sameAs links
     * @throws DatabaseException query error
     */
    private Collection<Statement> getSameAsLinks() throws DatabaseException {
        long startTime = System.currentTimeMillis();
        Collection<Statement> sameAsTriples = new ArrayList<Statement>();
        for (URI property : conflictResolutionPolicy.getPropertyResolutionStrategies().keySet()) {
            addSameAsLinksForURI(property.stringValue(), sameAsTriples);
        }
        LOG.debug("Query Execution: getSameAsLinks() in {} ms ({} links)",
                System.currentTimeMillis() - startTime, sameAsTriples.size());
        return sameAsTriples;
    }
}
