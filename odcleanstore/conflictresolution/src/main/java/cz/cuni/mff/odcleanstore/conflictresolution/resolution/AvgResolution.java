/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.MediatingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class AvgResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "AVG";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public AvgResolution(MediatingConfidenceCalculator confidenceCalculator) {
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
        Statement lastAggregableStatement = null;
        Value lastObject = null;
        for (Statement statement : statements) {
            double numberValue = ResolutionFunctionUtils.convertToDoubleSilent(statement.getObject());
            if (!Double.isNaN(numberValue)) {
                sum += numberValue;
                validCount++;
                lastAggregableStatement = statement;
            } else if (!CRUtils.sameValues(statement.getObject(), lastObject)){
                handleNonAggregableStatement(statement, statements, crContext, result);
            }
            lastObject = statement.getObject();
        }
        
        Set<Resource> sources = getAllSources(statements);
        
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
