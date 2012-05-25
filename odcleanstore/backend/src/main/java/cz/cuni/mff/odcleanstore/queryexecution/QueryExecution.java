package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.Utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
    private final SparqlEndpoint sparqlEndpoint;

    private final PrefixMappingCache prefixMappingCache;

    /**
     * Creates a new instance of QueryExecution.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    public QueryExecution(SparqlEndpoint sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.prefixMappingCache = new PrefixMappingCache(sparqlEndpoint);
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

        KeywordQueryExecutor queryExecutor = new KeywordQueryExecutor(sparqlEndpoint, constraints,
                expandPropertyNames(aggregationSpec), getExpandedDefaultConfiguration());
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

        String expandedURI = Utils.isPrefixedName(uri) ? getPrefixMapping().expandPrefix(uri) : uri;
        UriQueryExecutor queryExecutor = new UriQueryExecutor(sparqlEndpoint, constraints,
                expandPropertyNames(aggregationSpec), getExpandedDefaultConfiguration());
        return queryExecutor.findURI(expandedURI);
    }

    /**
     * Returns a (cached) prefix mapping.
     * @return prefix mapping
     * @throws QueryExecutionException database error
     */
    private PrefixMapping getPrefixMapping() throws QueryExecutionException {
        try {
            return prefixMappingCache.getCachedValue();
        } catch (DatabaseException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
        }
    }

    /**
     * Expands prefixed names in the given aggregation settings to full URIs.
     * TODO: optimize?
     * @param aggregationSpec aggregation settings where property names are expanded
     * @return new aggregation settings
     * @throws QueryExecutionException database error
     */
    private AggregationSpec expandPropertyNames(AggregationSpec aggregationSpec) throws QueryExecutionException {
        if (aggregationSpec.getPropertyAggregations().isEmpty() && aggregationSpec.getPropertyMultivalue().isEmpty()) {
            return aggregationSpec;
        }

        AggregationSpec result = aggregationSpec.createShallowCopy();

        Map<String, EnumAggregationType> newPropertyAggregations = new TreeMap<String, EnumAggregationType>();
        for (Entry<String, EnumAggregationType> entry : aggregationSpec.getPropertyAggregations().entrySet()) {
            String property = entry.getKey();
            if (Utils.isPrefixedName(property)) {
                newPropertyAggregations.put(getPrefixMapping().expandPrefix(property), entry.getValue());
            } else {
                newPropertyAggregations.put(property, entry.getValue());
            }
        }
        result.setPropertyAggregations(newPropertyAggregations);

        Map<String, Boolean> newPropertyMultivalue = new TreeMap<String, Boolean>();
        for (Entry<String, Boolean> entry : aggregationSpec.getPropertyMultivalue().entrySet()) {
            String property = entry.getKey();
            if (Utils.isPrefixedName(property)) {
                newPropertyMultivalue.put(getPrefixMapping().expandPrefix(property), entry.getValue());
            } else {
                newPropertyMultivalue.put(property, entry.getValue());
            }
        }
        result.setPropertyMultivalue(newPropertyMultivalue);

        return result;
    }

    /** TODO. */
    private static final AggregationSpec DEFAULT_CONFIGURATION = new AggregationSpec();
    /** TODO. @return TODO */
    private AggregationSpec getExpandedDefaultConfiguration() {
        return DEFAULT_CONFIGURATION;
    }
}
