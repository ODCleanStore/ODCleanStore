package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;

import com.hp.hpl.jena.graph.Triple;

import java.util.Collection;

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
    public QuadCollection findKeyword(String keyword, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        return null;
    }

    private QuadCollection getKeywordOccurences() {
        return null;
    }

    private QuadCollection getLabels() {
        return null;
    }

    private NamedGraphMetadataMap getMetadata() {
        return null;
    }

    /**
     * @todo return a different type
     */
    private Collection<Triple> getSameAsLinks() {
        return null;
    }
}
