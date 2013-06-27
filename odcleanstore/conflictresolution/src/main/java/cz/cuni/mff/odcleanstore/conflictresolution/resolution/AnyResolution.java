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
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public class AnyResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "ANY";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public AnyResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
     
        Statement first = statements.iterator().next();
        Set<Resource> sources = filterSources(first, statements);
        double fQuality = getFQuality(first.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                first.getSubject(),
                first.getPredicate(),
                first.getObject(),
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
