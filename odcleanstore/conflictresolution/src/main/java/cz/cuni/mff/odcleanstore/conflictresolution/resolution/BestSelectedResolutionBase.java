/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedComparator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Base class for deciding resolution functions which select a single value based on a simple comparison of some values.
 * @param <T> type of compared values
 * @author Jan Michelfeit
 */
public abstract class BestSelectedResolutionBase<T> extends DecidingResolutionFunction {
    private static final int INITIAL_RESULT_CAPACITY = 5; // expect few non-aggregable statements
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    protected BestSelectedResolutionBase(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(INITIAL_RESULT_CAPACITY);
        BestSelectedComparator<T> comparator = getComparator(statements, crContext);
        
        Value lastObject = null;
        Statement bestStatement = null; // the best quad so far
        T bestComparedValue = null;
        for (Statement statement : statements) {
            T comparedValue = getComparedValue(statement, crContext);
            if (!comparator.accept(comparedValue, crContext) 
                    // check for same objects so that non-aggregable statements are only once in the result;
                    // relies on statements being spog-sorted
                    && !CRUtils.sameValues(statement.getObject(), lastObject)) { 
                handleNonAggregableStatement(statement, statements, crContext, result);
            } else if (bestComparedValue == null || comparator.compare(comparedValue, bestComparedValue, crContext) > 0) {
                bestComparedValue = comparedValue;
                bestStatement = statement;
            }
            lastObject = statement.getObject();
        }
        
        if (bestStatement == null) {
            // no accepted statement
            return result;
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double fQuality = getFQuality(bestStatement.getObject(), sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                fQuality,
                sources);
        result.add(resolvedStatement);
        return result;
    }

    /**
     * Returns a comparator of quads that orders the best quad as having the highest order.
     * @param statements statements to be resolved
     * @param crContext context of conflict resolution
     * @return a comparator of values that orders the best quad as having the highest order
     */
    protected abstract BestSelectedComparator<T> getComparator(Model statements, CRContext crContext);
    
    /**
     * Returns the value to be compared for the given quad (e.g. its object).
     * @param statement statement to be compared
     * @param crContext conflict resolution context
     * @return value to be compared
     */
    protected abstract T getComparedValue(Statement statement, CRContext crContext);
}
