package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.Collection;

/**
 * A triple/quad aggregation method.
 * 
 * @author Jan Michelfeit
 */
public interface AggregationMethod {
    /**
     * Aggregates quads in conflictingQuads into one or more result quads and
     * calculates quality estimate and source information for each of the result
     * quads.
     * 
     * The aggregation can be based on values in the input quads, their respective
     * named graph metadata (passed in #metadata) and the selected aggregation
     * error strategy (see {@link AggregationErrorStrategy}).
     * The result quads may include some of the input quads or contain completely
     * new quads.
     * 
     * By conflicting quads we mean quads that have the same subject and predicate,
     * but not necesserily different objects (i.e. some of them may be identical).
     * 
     * @param conflictingQuads input quads to be aggregated
     * @param metadata metadata for named graphs occuring in conflictingQuads
     * @param errorStrategy strategy to be used when the aggregation is not
     *        applicable to a quad in conflictingQuads
     * @param uriGenerator generator of URIs
     * @return aggregated quads together with quality estimate and source
     *         information for each quad
     * @todo do something with that many parameters
     */
    Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator);
}
