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
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class SumResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "SUM";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public SumResolution(MediatingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) { 
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(1);
        
        // Compute average value
        double sum = 0;
        Statement lastAggregableStatement = null;
        Value lastObject = null;
        for (Statement statement : statements) {
            double numberValue = ResolutionFunctionUtils.convertToDoubleSilent(statement.getObject());
            if (!Double.isNaN(numberValue)) {
                sum += numberValue;
                lastAggregableStatement = statement;
            } else if (!CRUtils.sameValues(statement.getObject(), lastObject)) {
                handleNonAggregableStatement(statement, statements, crContext, result);
            }
            lastObject = statement.getObject();
        }

        Set<Resource> sources = getAllSources(statements);

        Literal sumLiteral = crContext.getResolvedStatementFactory().getValueFactory().createLiteral(sum);
        double fQuality = getFQuality(sumLiteral, statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                lastAggregableStatement.getSubject(),
                lastAggregableStatement.getPredicate(),
                sumLiteral,
                fQuality,
                sources);
        result.add(resolvedStatement);

        return result;
    }
}
