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
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public class BestSourceResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "BEST_SOURCE";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private final SourceQualityCalculator sourceQualityCalculator;
    
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
        double fQuality = getFQuality(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }        
}
