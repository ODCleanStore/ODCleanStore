package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

/**
 * A triple/quad aggregation method.
 *
 * @author Jan Michelfeit
 */
public interface ObjectAggregationMethod {
    /**
     * Aggregates quads in conflictingQuads into one or more result quads and
     * calculates quality estimate and source information for each of the result
     * quads.
     *
     * The aggregation can be based on values in the input quads, their respective
     * named graph metadata (passed in #metadata) and the given aggregation settings
     * The result quads may include some of the input quads or contain completely
     * new quads.
     *
     * By conflicting quads we mean quads that have the same subject and predicate,
     * but not necessarily different objects (i.e. some of them may be identical).
     *
     * @param conflictingQuads input quads to be aggregated
     * @param metadata metadata for named graphs containing the quads being resolved
     * @return aggregated quads together with quality estimate and source
     *         information for each quad
     */
    Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata);
}
