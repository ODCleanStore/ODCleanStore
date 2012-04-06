package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

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
    public NamedGraphSet findKeyword(String keyword, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        return null;
    }

    /**
     *
     * @param uri
     * @param constraints
     * @param aggregationSpec
     */
    public NamedGraphSet findURI(String uri, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        return null;
    }

}
