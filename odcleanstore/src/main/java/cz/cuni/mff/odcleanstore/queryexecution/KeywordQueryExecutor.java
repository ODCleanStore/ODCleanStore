package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.Graph;

/**
 * @author Jan Michelfeit
 */
public class KeywordQueryExecutor extends QueryExecutor {
    
    public KeywordQueryExecutor(QueryConstraintSpec queryConstraints) {
        super(queryConstraints);
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

    private QuadGraph getKeywordOccurences() {
        return null;
    }

    private QuadGraph getLabels() {
        return null;
    }

    private NamedGraphMetadataMap getMetadata() {
        return null;
    }

    private Graph getSameAsLinks() {
        return null;
    }
}
