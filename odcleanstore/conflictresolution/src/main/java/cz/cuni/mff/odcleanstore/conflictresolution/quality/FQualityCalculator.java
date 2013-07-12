/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * Calculator of F-quality ("quality of fused data") of
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement resolved quads}.
 * F-Quality is a number from interval [0; 1] which expresses quality of the resolved statement with respect to other conflicting
 * statements, provenance of the statements, and quality-related metadata.
 * 
 * @author Jan Michelfeit
 */
public interface FQualityCalculator {
    /**
     * Returns the F-quality ("quality of fused data") for the given value in the given conflict resolution context.
     * @param value estimated value - object of the statement to be estimated 
     * @param conflictingStatements other statements that are (potentially) conflicting with the statement to be estimated
     * @param sources collection of sources (their named graph URIs) claiming the given value
     * @param crContext context of conflict resolution
     * @return F-quality for <code>value</code>
     */
    double getFQuality(Value value, Collection<Statement> conflictingStatements, 
            Collection<Resource> sources, CRContext crContext);
}
