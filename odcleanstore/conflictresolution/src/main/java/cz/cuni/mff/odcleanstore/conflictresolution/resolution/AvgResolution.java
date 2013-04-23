/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class AvgResolution extends MediatingResolutionFunction {
    protected AvgResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) { 
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(1);
        
        // Compute average value
        double sum = 0;
        double validCount = 0;
        Collection<Resource> sources = new ArrayList<Resource>(statements.size() / 2);
        Statement lastAggregableStatement = null;
        for (Statement statement : statements) {
            double numberValue = ResolutionFunctionUtils.convertToDoubleSilent(statement.getObject());
            if (!Double.isNaN(numberValue)) {
                sum += numberValue;
                validCount++;
                sources.add(statement.getContext());
                lastAggregableStatement = statement;
            } else {
                handleNonAggregableStatement(statement, statements, crContext, result);
            }
        }
        
        if (validCount > 0) {
            double averageValue = sum / validCount;
            Literal averageLiteral = crContext.getResolvedStatementFactory().getValueFactory().createLiteral(averageValue);
            double confidence = getConfidence(averageLiteral, statements, sources, crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    lastAggregableStatement.getSubject(),
                    lastAggregableStatement.getPredicate(),
                    averageLiteral,
                    confidence,
                    sources);
            result.add(resolvedStatement);
        }
        
        return result;
    }
}
