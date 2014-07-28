/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.SmallContextsSet;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;

import java.util.Set;

/**
 * Base class for mediating conflict resolution functions (i.e. those that can produce values not included in its input).
 * @author Jan Michelfeit
 */
public abstract class MediatingResolutionFunction extends ResolutionFunctionBase {
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads}
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator}) 
     */
    protected MediatingResolutionFunction(MediatingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    /**
     * Returns a set of all named graphs of quads in the given model.
     * @param statements statements whose named graphs are added to result
     * @return set of source graph names
     */
    protected final Set<Resource> getAllSources(Model statements) {
        return SmallContextsSet.fromIterator(statements.iterator());
    }
}
