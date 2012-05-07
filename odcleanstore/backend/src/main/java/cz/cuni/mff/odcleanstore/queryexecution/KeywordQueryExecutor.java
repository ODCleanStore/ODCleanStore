package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes the keyword search query.
 * Triples that contain the given keywords (separated by whitespace) in the object of the triple
 * of type literal are returned.
 *
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
/*package*/class KeywordQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(KeywordQueryExecutor.class);

    /** Maximum allowed length of the query. */
    public static final int MAX_QUERY_LENGTH = 1024;

    /**
     * SPARQL query that gets the main result quads.
     * Use of UNION instead of a more complex filter is to make owl:sameAs inference in Virtuoso work.
     * The subquery is necessary to make Virtuoso translate subjects/objects to a single owl:sameAs equivalent.
     * This way we don't need to obtain owl:sameAs links for subjects/objects (passed to ConflictResolverSpec) from
     * the database explicitly.
     *
     * The query must be formatted with these arguments: (1) bif:contains match expressionURI, (2) exact match
     * expression, (3) graph filter clause, (4) limit
     *
     * TODO: Possible optimization - if query doesn't match {@link #NUMERIC_PATTERN} nor {@link #XSD_DATETIME_PATTERN},
     * than the whole exact match part can be dropped (bif:contains will work for both plain literals and xsd:string).
     */
    private static final String KEYWORD_OCCURENCES_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?graph ?s ?p ?o"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           ?o bif:contains %1$s"
            // + "\n           OPTION (score ?sc)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?o = %2$s)"
            + "\n         }"
            + "\n       }"
            + "\n       %3$s"
            + "\n     }"
            + "\n     LIMIT %4$d"
            + "\n   }"
            + "\n }";
    // + "\n ORDER BY DESC(IF(bif:isnull(?sc), 100000, ?sc))";

    /**
     * SPARQL query that gets relevant owl:sameAs links for conflict resolution of the result quads.
     * Returns only links for properties explicitly listed in aggregation settings.
     *
     * The query must be formatted with these arguments: (1) bif:contains match expressionURI, (2) exact match
     * expression, (3) graph filter clause, (4) list of properties (separated by ','), (5) limit.
     *
     * @see #URI_OCCURENCES_QUERY
     */
    private static final String SAME_AS_LINKS_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?p ?linked"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           ?o bif:contains %1$s"
            // + "\n           OPTION (score ?sc)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?o = %2$s)"
            + "\n         }"
            + "\n       }"
            + "\n       %3$s"
            + "\n     }"
            + "\n     LIMIT %5$d"
            + "\n   }"
            + "\n   ?linked owl:sameAs ?p."
            + "\n   FILTER (?linked IN (%4$s))"
            + "\n }"
            + "\n LIMIT %5$d";

    /**
     * SPARQL query that gets metadata for named graphs containing result quads.
     * Source is the only required value, others can be null.
     * For the reason why UNIONs and subqueries are used, see {@link #URI_OCCURENCES_QUERY}.
     *
     * OPTIONAL clauses for fetching ?graph properties are necessary (probably due to Virtuoso inference processing).
     *
     * Must be formatted with arguments: (1) bif:contains match expressionURI, (2) exact match
     * expression, (3) graph filter clause, (4) label properties, (5) resGraph prefix filter, (6) limit
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
            + "\n             ?s ?p ?o."
            + "\n             ?o bif:contains %1$s"
            // + "\n             OPTION (score ?sc)"
            + "\n           }"
            + "\n         }"
            + "\n         UNION"
            + "\n         {"
            + "\n           GRAPH ?graph {"
            + "\n             ?s ?p ?o."
            + "\n             FILTER (?o = %2$s)"
            + "\n           }"
            + "\n         }"
            + "\n         %3$s"
            + "\n       }"
            + "\n       LIMIT %6$d"
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
            + "\n                 ?r ?p ?o."
            + "\n                 ?o bif:contains %1$s"
            // + "\n                 OPTION (score ?sc)"
            + "\n               }"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?r ?p ?o."
            + "\n                 FILTER (?o = %2$s)"
            + "\n               }"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?s ?r ?o."
            + "\n                 ?o bif:contains %1$s"
            // + "\n                 OPTION (score ?sc)"
            + "\n               }"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?graph {"
            + "\n                 ?s ?r ?o."
            + "\n                 FILTER (?o = %2$s)"
            + "\n               }"
            + "\n             }"
            + "\n             %3$s"
            + "\n           }"
            + "\n           LIMIT %6$d"
            + "\n         }"
            + "\n         GRAPH ?resGraph {"
            + "\n           ?r ?labelProp ?label"
            + "\n         }"
            + "\n         FILTER (?labelProp IN (%4$s))"
            + "\n       }"
            + "\n       LIMIT %6$d"
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
            + "\n   %5$s"
            + "\n   FILTER (bound(?source))"
            + "\n }"
            + "\n LIMIT %6$d";

    /**
     * SPARQL query for retrieving labels of resources contained in the result, except for the searched URI
     * (we get that by {@link #URI_OCCURENCES_QUERY}).
     *
     * For the reason why UNIONs and subqueries are used, see {@link #URI_OCCURENCES_QUERY}.
     *
     * Must be formatted with arguments: (1) bif:contains match expressionURI, (2) exact match
     * expression, (3) graph filter clause, (4) label properties, (5) ?labelGraph prefix filter, (6) limit.
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
            + "\n           ?r ?p ?o."
            + "\n           ?o bif:contains %1$s"
            // + "\n           OPTION (score ?sc)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?r ?p ?o."
            + "\n           FILTER (?o = %2$s)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           ?o bif:contains %1$s"
            // + "\n           OPTION (score ?sc)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           FILTER (?o = %2$s)"
            + "\n         }"
            + "\n       }"
            + "\n       %3$s"
            + "\n     }"
            + "\n     LIMIT %6$d"
            + "\n   }"
            + "\n   GRAPH ?labelGraph {"
            + "\n     ?r ?labelProp ?label"
            + "\n   }"
            + "\n   FILTER (?labelProp IN (%4$s))"
            + "\n   %5$s"
            + "\n }"
            + "\n LIMIT %6$d"
            + "\n }}";

    /**
     * Pattern matching characters removed from the searched keyword for bif:contains match.
     * Must remove single quotes but doesn't remove double quotes since they are used to denote phrases with whitespace.
     * @see #getContainsMatchExpr(String)
     */
    private static final Pattern CONTAINS_FILTER_PATTERN = Pattern.compile("[\\x00-\\x09\\x0E-\\x1F'`]+");

    /**
     * Pattern matching one keyword in the input query.
     * Keywords are separated by whitespace and/or can be enclosed in double quotes (in that case a keyword may
     * contain whitespace).
     */
    private static final Pattern CONTAINS_KEYWORD_PATTERN = Pattern.compile("\"[^\"]+\"\\s*|[^\"\\s]+\\s*");

    /**
     * Pattern matching characters removed from the searched keyword for exact match (equality comparison).
     * Must remove quotes (single and double).
     * @see #getExactMatchExpr(String)
     */
    private static final Pattern EXACT_MATCH_FILTER_PATTERN = Pattern.compile("[\\x00-\\x09\\x0E-\\x1F\"'`]+");

    // CHECKSTYLE:OFF
    /** Pattern matching a valid xsd:dateTime value. */
    private static final Pattern XSD_DATETIME_PATTERN = Pattern.compile("^-?[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?(Z|[+-][0-1][0-9]:[0-5][0-9])?$");
    // CHECKSTYLE:ON

    /** Pattern matching a numeric value. */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[+-]?[0-9]*\\.?[0-9]+$");

    /**
     * Builds an expression that matches the given keyword(s) in a bif:contains pattern.
     * Returns an empty string if no valid keyword is in keywordsQuery.
     * The result is an expression matching a conjunction of all keywords in keywordsQuery which can be either enclosed
     * in double quotes and/or separated by whitespace.
     *
     * TODO: possible tests: "abc def \"efg\" hij", "abc\"def\"efg", "abc\"def", "a'bc", "\"abc
     *
     * @param keywordsQuery the keyword query
     * @return an expression that matches the given keyword(s) in a bif:contains pattern
     */
    private static String buildContainsMatchExpr(String keywordsQuery) {
        String filteredKeywordsQuery = CONTAINS_FILTER_PATTERN.matcher(keywordsQuery).replaceAll("");
        Matcher keywordMatcher = CONTAINS_KEYWORD_PATTERN.matcher(filteredKeywordsQuery);

        StringBuilder expr = new StringBuilder();
        expr.append('\'');
        boolean hasMatch = false;
        while (keywordMatcher.find()) {
            if (!hasMatch) {
                hasMatch = true;
            } else {
                expr.append(" AND ");
            }

            String keyword = keywordMatcher.group().trim();
            if (keyword.startsWith("\"")) {
                assert keyword.length() > 2;
                expr.append(keyword);
            } else {
                assert keyword.length() > 0;
                expr.append('"');
                expr.append(keyword);
                expr.append('"');
            }
        }
        expr.append('\'');
        return hasMatch ? expr.toString() : "";
    }

    /**
     * Returns an expression that equals the whole keywordsQuery considering a possible type of the literal.
     * If the query looks like a numeric literal or xsd:dateTime literal, returns a typed literal, otherwise returns
     * a quoted string (with quotes inside the query filtered out).
     *
     * If the searched string is not a typed literal, it should still be included in the result by the bif:contains
     * match. The exact match cannot be str(?o) = "..." due to lower performance.
     *
     * @see http://www.w3.org/TR/rdf-sparql-query/#operandDataTypes
     * @param keywordsQuery the keyword query
     * @return a literal value equal to the given keyword, considering type
     */
    private static String buildExactMatchExpr(String keywordsQuery) {
        if (NUMERIC_PATTERN.matcher(keywordsQuery).matches()) {
            return keywordsQuery;
        }
        Matcher dateTimeMatcher = XSD_DATETIME_PATTERN.matcher(keywordsQuery);
        if (dateTimeMatcher.matches()) {
            return (dateTimeMatcher.group(2) != null)
                    ? '"' + keywordsQuery + "\"^^<" + XMLSchema.dateTimeType + '>'
                    : '"' + keywordsQuery + "Z\"^^<" + XMLSchema.dateTimeType + '>'; // Virtuoso won't match without 'Z'
        }
        return '"' + EXACT_MATCH_FILTER_PATTERN.matcher(keywordsQuery).replaceAll("") + '"';
    }

    /**
     * Creates a new instance of KeywordQueryExecutor.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     */
    public KeywordQueryExecutor(SparqlEndpoint sparqlEndpoint, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        super(sparqlEndpoint, constraints, aggregationSpec);
    }

    /**
     * Executes the keyword search query.
     *
     * @param keywordsQuery searched keywords (separated by whitespace)
     * @return query result holder
     * @throws ODCleanStoreException database error
     */
    public QueryResult findKeyword(String keywordsQuery) throws ODCleanStoreException {
        LOG.info("Keyword query for '{}'", keywordsQuery);
        long startTime = System.currentTimeMillis();
        checkValidSettings();

        if (keywordsQuery.length() > MAX_QUERY_LENGTH) {
            throw new QueryException("The requested keyword query is longer than " + MAX_QUERY_LENGTH + " characters.");
        }

        String containsMatchExpr = buildContainsMatchExpr(keywordsQuery);
        String exactMatchExpr = buildExactMatchExpr(keywordsQuery);
        if (containsMatchExpr.isEmpty() || exactMatchExpr.isEmpty()) {
            // No valid keywords
            return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(),
                    System.currentTimeMillis() - startTime);
        }
        try {
            // Get the quads relevant for the query
            Collection<Quad> quads = getKeywordOccurrences(containsMatchExpr, exactMatchExpr);
            if (quads.isEmpty()) {
                return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(),
                        System.currentTimeMillis() - startTime);
            }
            quads.addAll(getLabels(containsMatchExpr, exactMatchExpr));

            // Gather all settings for Conflict Resolution
            ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
            crSpec.setPreferredURIs(getPreferredURIs());
            crSpec.setSameAsLinks(getSameAsLinks(containsMatchExpr, exactMatchExpr).iterator());
            NamedGraphMetadataMap metadata = getMetadata(containsMatchExpr, exactMatchExpr);
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
     * Returns preferred URIs for the result.
     * These include the properties explicitly listed in aggregation settings.
     * @return preferred URIs
     */
    private Set<String> getPreferredURIs() {
        Set<String> aggregationProperties = aggregationSpec.getPropertyAggregations() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyAggregations().keySet();
        Set<String> multivalueProperties = aggregationSpec.getPropertyMultivalue() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyMultivalue().keySet();
        if (aggregationProperties.isEmpty() && multivalueProperties.isEmpty()) {
            return Collections.<String>emptySet();
        }
        Set<String> preferredURIs = new HashSet<String>(aggregationProperties.size() + multivalueProperties.size());
        preferredURIs.addAll(aggregationProperties);
        preferredURIs.addAll(multivalueProperties);
        return preferredURIs;
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

        LOG.debug("Query Execution: findKeyword() in {} ms", executionTime);
        // Format and return result
        QueryResult queryResult = new QueryResult(resultQuads, metadata, EnumQueryType.KEYWORD, constraints,
                aggregationSpec);
        queryResult.setExecutionTime(executionTime);
        return queryResult;
    }

    /**
     * Return a collection of quads relevant for the query (without metadata or any additional quads).
     * @param containsMatchExpr an expression for bif:matches matching the searched keyword(s)
     * @param exactMatchExpr a value matching the searched keyword for equality
     * @return retrieved quads
     * @throws ODCleanStoreException query error
     */
    private Collection<Quad> getKeywordOccurrences(String containsMatchExpr, String exactMatchExpr)
            throws ODCleanStoreException {
        String query = String.format(KEYWORD_OCCURENCES_QUERY, containsMatchExpr, exactMatchExpr,
                getGraphFilterClause(), MAX_LIMIT);
        return getQuadsFromQuery(query, "getKeywordOccurrences()");
    }

    /**
     * Return labels of resources returned by {{@link #getKeywordOccurrences(String)} as quads.
     * @param containsMatchExpr an expression for bif:matches matching the searched keyword(s)
     * @param exactMatchExpr a value matching the searched keyword for equality
     * @return labels as quads
     * @throws ODCleanStoreException query error
     */
    private Collection<Quad> getLabels(String containsMatchExpr, String exactMatchExpr) throws ODCleanStoreException {
        String query = String.format(Locale.ROOT, LABELS_QUERY, containsMatchExpr, exactMatchExpr,
                getGraphFilterClause(), LABEL_PROPERTIES_LIST, getGraphPrefixFilter("labelGraph"), MAX_LIMIT);
        return getQuadsFromQuery(query, "getLabels()");
    }

    /**
     * Return metadata for named graphs containing quads returned in the result.
     * @param containsMatchExpr an expression for bif:matches matching the searched keyword(s)
     * @param exactMatchExpr a value matching the searched keyword for equality
     * @return metadata of result named graphs
     * @throws ODCleanStoreException query error
     */
    private NamedGraphMetadataMap getMetadata(String containsMatchExpr, String exactMatchExpr)
            throws ODCleanStoreException {
        String query = String.format(Locale.ROOT, METADATA_QUERY, containsMatchExpr, exactMatchExpr,
                getGraphFilterClause(), LABEL_PROPERTIES_LIST, getGraphPrefixFilter("resGraph"), MAX_LIMIT);
        return getMetadataFromQuery(query, "getMetadata()");
    }

    /**
     * Returns owl:sameAs links relevant for conflict resolution for this query.
     * Returns only links for properties explicitly listed in aggregation settings;
     * other links (e.g. between subjects/objects in the result) are resolved by Virtuoso.
     * @param containsMatchExpr an expression for bif:matches matching the searched keyword(s)
     * @param exactMatchExpr a value matching the searched keyword for equality
     * @see #KEYWORD_OCCURENCES_QUERY
     * @return collection of relevant owl:sameAs links
     * @throws ODCleanStoreException query error
     */
    private Collection<Triple> getSameAsLinks(String containsMatchExpr, String exactMatchExpr)
            throws ODCleanStoreException {
        Set<String> aggregationProperties = aggregationSpec.getPropertyAggregations() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyAggregations().keySet();
        Set<String> multivalueProperties = aggregationSpec.getPropertyMultivalue() == null
                ? Collections.<String>emptySet()
                : aggregationSpec.getPropertyMultivalue().keySet();
        if (aggregationProperties.isEmpty() && multivalueProperties.isEmpty()) {
            // Nothing to get sameAs links for
            return Collections.<Triple>emptySet();
        }

        long startTime = System.currentTimeMillis();

        // Build query
        final String separator = ", ";
        StringBuilder properties = new StringBuilder();
        for (String property : aggregationProperties) {
            properties.append('<');
            properties.append(property);
            properties.append('>');
            properties.append(separator);
        }
        for (String property : multivalueProperties) {
            properties.append('<');
            properties.append(property);
            properties.append('>');
            properties.append(separator);
        }
        assert properties.length() >= separator.length(); // there is at least one property
        properties.setLength(properties.length() - separator.length()); // trim the last separator
        String query = String.format(SAME_AS_LINKS_QUERY, containsMatchExpr, exactMatchExpr, getGraphFilterClause(),
                properties, MAX_LIMIT);

        // Execute query
        WrappedResultSet resultSet = getConnection().executeSelect(query);
        LOG.debug("Query Execution: getSameAsLinks() query took {} ms", System.currentTimeMillis() - startTime);

        // Create sameAs triples
        try {
            Collection<Triple> sameAsTriples = new ArrayList<Triple>();
            while (resultSet.next()) {
                Triple triple = Triple.create(
                        resultSet.getNode(1),
                        SAME_AS_PROPERTY,
                        resultSet.getNode(2));
                sameAsTriples.add(triple);
            }

            LOG.debug("Query Execution: getSameAsLinks() in {} ms", System.currentTimeMillis() - startTime);
            return sameAsTriples;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            resultSet.closeQuietly();
        }
    }
}
