package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.vocabulary.RDFS;

/**
 * The base class of query executors.
 *
 * Each query executor loads triples relevant for the query and metadata from the clean database, applies
 * conflict resolution to it and returns a holder of thr result quads and metadata.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class QueryExecutorBase {
    /**
     * (Debug) Only named graph having URI starting with this prefix can be included in query result.
     * If the value is null, there is now restriction on named graph URIs.
     * This constant is only for debugging purposes and should be null in production environment.
     * TODO: set to null
     */
    protected static final String GRAPH_PREFIX_FILTER = "http://odcs.mff.cuni.cz/namedGraph/qe-test/";

    /**
     * Maximum number of triples returned by each database query (the overall result size may be larger).
     * TODO: get from global configuration.
     */
    protected static final long MAX_LIMIT = 500;

    /**
     * Prefix of named graphs where the resulting triples are placed.
     * TODO: get from global configuration.
     */
    protected static final String RESULT_GRAPH_PREFIX = "http://odcs.mff.cuni.cz/results/";

    /** Properties designating a human-readable label. */
    protected static final String[] LABEL_PROPERTIES = new String[] { RDFS.label };

    /** List of {@link #LABEL_PROPERTIES} formatted to a string for use in a SPARQL query. */
    protected static final String LABEL_PROPERTIES_LIST;

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

    // CHECKSTYLE:OFF
    /** Connection settings for the SPARQL endpoint that will be queried. */
    protected final SparqlEndpoint sparqlEndpoint;

    /** Constraints on triples returned in the result. */
    protected final QueryConstraintSpec constraints;

    /** Aggregation settings for conflict resolution. */
    protected final AggregationSpec aggregationSpec;
    // CHECKSTYLE:ON

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
}
