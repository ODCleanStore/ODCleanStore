package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.Graph;

/**
 * @author Jan Michelfeit
 */
public class UriQueryExecutor extends QueryExecutor {

    public UriQueryExecutor(QueryConstraintSpec queryConstraints) {
        super(queryConstraints);
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

    private QuadGraph getURIOccurences() {
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
