/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Resolution function selecting a quad with the best quality score of its source named graph.
 * In case of tie, the first quad is selected.
 * @author Jan Michelfeit
 */
public class BestSourceResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "BEST_SOURCE";
    
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
    
    private final SourceQualityCalculator sourceQualityCalculator;
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator})
     * @param sourceQualityCalculator calculator of quality score of quad source named graphs 
     */
    public BestSourceResolution(DecidingFQualityCalculator fQualityCalculator, SourceQualityCalculator sourceQualityCalculator) {
        super(fQualityCalculator);
        this.sourceQualityCalculator = sourceQualityCalculator;
    } 

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        Statement bestStatement = null;
        double bestSourceQuality = Double.NEGATIVE_INFINITY;
        for (Statement statement : statements) {
            if (bestStatement == null) {
                bestStatement = statement;
                continue;
            }
            double quality = sourceQualityCalculator.getSourceQuality(statement.getContext(), crContext.getMetadata());
            if (quality > bestSourceQuality) {
                bestStatement = statement;
                bestSourceQuality = quality;
            }
        }
        
        if (bestStatement == null) {
            return Collections.emptySet();
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double fQuality = getFQuality(bestStatement.getObject(), sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }        
}
