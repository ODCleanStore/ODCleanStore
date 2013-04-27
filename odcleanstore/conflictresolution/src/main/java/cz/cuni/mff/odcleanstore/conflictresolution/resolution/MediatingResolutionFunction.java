/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.confidence.MediatingConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class MediatingResolutionFunction extends ResolutionFunctionBase {
    protected MediatingResolutionFunction(MediatingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    protected final Set<Resource> getAllSources(Model statements) {
        int initialCapacity = Math.max((int) (statements.size() / .75f) + 1, 8);
        Set<Resource> sources = new HashSet<Resource>(initialCapacity);
        for (Statement statement : statements) {
            sources.add(statement.getContext());
        }
        return sources;
    }
}
