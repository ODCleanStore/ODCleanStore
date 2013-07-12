/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;

/**
 * Base class for mediating conflict resolution functions (i.e. those that can produce values not included in its input).
 * @author Jan Michelfeit
 */
public abstract class MediatingResolutionFunction extends ResolutionFunctionBase {
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link z.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads} 
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
        // CHECKSTYLE:OFF
        int initialCapacity = Math.max((int) (statements.size() / .75f) + 1, 8);
        // CHECKSTYLE:ON
        Set<Resource> sources = new HashSet<Resource>(initialCapacity);
        for (Statement statement : statements) {
            sources.add(statement.getContext());
        }
        return sources;
    }
}
