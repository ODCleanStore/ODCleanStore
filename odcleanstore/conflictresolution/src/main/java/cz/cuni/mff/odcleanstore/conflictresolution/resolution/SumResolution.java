/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import org.openrdf.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Returns statement with the sum of all values in place of objects of quads to be resolved. 
 * Only quads with numeric literal as object can be aggregated by this function.
 * @author Jan Michelfeit
 */
public class SumResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "SUM";
    
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
        double fQuality = getFQuality(sumLiteral, sources, crContext);
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
