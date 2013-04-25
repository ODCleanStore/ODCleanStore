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
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class BestSourceResolution extends DecidingResolutionFunction {
    private final SourceConfidenceCalculator sourceConfidenceCalculator;
    
    protected BestSourceResolution(ConfidenceCalculator confidenceCalculator, SourceConfidenceCalculator sourceConfidenceCalculator) {
        super(confidenceCalculator);
        this.sourceConfidenceCalculator = sourceConfidenceCalculator;
    } 

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        Statement bestStatement = null;
        double bestSourceConfidence = Double.NEGATIVE_INFINITY;
        for (Statement statement : statements) {
            if (bestStatement == null) {
                bestStatement = statement;
                continue;
            }
            double confidence = sourceConfidenceCalculator.sourceConfidence(statement.getContext(), crContext.getMetadata());
            if (confidence > bestSourceConfidence) {
                bestStatement = statement;
                bestSourceConfidence = confidence;
            }
        }
        
        if (bestStatement == null) {
            return Collections.emptySet();
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double confidence = getConfidence(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                confidence,
                sources);
        return Collections.singleton(resolvedStatement);
    }        
}
