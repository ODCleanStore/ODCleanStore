/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;

/**
 * @author Jan Michelfeit
 */
public class AnyResolution extends DecidingResolutionFunction {
    protected AnyResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
     
        Statement first = statements.iterator().next();
        Set<Resource> sources = filterSources(first, statements);
        double confidence = getConfidence(first.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                first.getSubject(),
                first.getPredicate(),
                first.getObject(),
                confidence,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
