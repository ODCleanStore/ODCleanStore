package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Executes the keyword search query.
 * Triples that contain the given keywords (separated by whitespace) in the object of the triple
 * of type literal are returned.
 *
 * @author Jan Michelfeit
 */
/*package*/class KeywordQueryExecutor extends QueryExecutorBase {

    /**
     * Executes the keyword search query.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     */
    public NamedGraphSet findKeyword(String keywords, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        return null;
    }

    private Collection<Quad> getKeywordOccurrences() {
        return null;
    }

    private Collection<Quad> getLabels() {
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