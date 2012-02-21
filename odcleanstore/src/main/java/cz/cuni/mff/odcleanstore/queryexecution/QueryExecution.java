package cz.cuni.mff.odcleanstore.queryexecution;


import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;

/**
 * @author Jan Michelfeit
 */
public class QueryExecution {

    public QueryExecution() {
    }

    /**
     * 
     * @param keyword
     * @param constraints
     * @param aggregationSpec
     */
    public QuadGraph findKeyword(String keyword, QueryConstraintSpec constraints, AggregationSpec aggregationSpec) {
        return null;
    }

    /**
     * 
     * @param uri
     * @param constraints
     * @param aggregationSpec
     */
    public QuadGraph findURI(String uri, QueryConstraintSpec constraints, AggregationSpec aggregationSpec) {
        return null;
    }

}