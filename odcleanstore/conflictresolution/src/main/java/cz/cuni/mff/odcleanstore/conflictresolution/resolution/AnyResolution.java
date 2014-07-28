/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Resolution function returning an arbitrary quad selected from the input. 
 * The implementation returns the first quad.
 * @author Jan Michelfeit
 */
public class AnyResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "ANY";
    
    /**
     * Returns a string identifier of this resolution function ({@value #FUNCTION_NAME}) - can be used to 
     * retrieve the resolution function from the default initialized 
     * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry}.
     * @see cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory#createInitializedResolutionFunctionRegistry()
     * @return string identifier of this resolution function
     */
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads}
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
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
        double fQuality = getFQuality(first.getObject(), sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                first.getSubject(),
                first.getPredicate(),
                first.getObject(),
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
