/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparator;

/**
 * @author Jan Michelfeit
 */
public abstract class BestSelectedResolutionBase extends DecidingResolutionFunction {
    private static final int INITIAL_RESULT_CAPACITY = 5; // expect few non-aggregable statements
    
    protected BestSelectedResolutionBase(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(INITIAL_RESULT_CAPACITY);
        LiteralComparator comparator = getComparator(statements, crContext);
        
        Value lastObject = null;
        Statement bestStatement = null; // the best quad so far
        for (Statement statement : statements) {
            if (!comparator.accept(statement.getObject(), crContext) 
                    // check for same objects so that non-aggregable statements are only once in the result;
                    // relies on statements being spog-sorted
                    && !CRUtils.sameValues(statement.getObject(), lastObject)) { 
                handleNonAggregableStatement(statement, statements, crContext, result);
            } else if (bestStatement == null || comparator.compare(statement.getObject(), bestStatement.getObject()) > 0) {
                bestStatement = statement;
            }
            lastObject = statement.getObject();
        }
        
        if (bestStatement == null) {
            // no accepted statement
            return result;
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double confidence = getConfidence(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                confidence,
                sources);
        result.add(resolvedStatement);
        return result;
    }

    /**
     * Returns a comparator of quads that orders the best quad as having the highest order.
     * @param crContext TODO
     * @param conflictingQuads input quads to be aggregated
     * @return a comparator of quads that orders the best quad as having the highest order
     */
    protected abstract LiteralComparator getComparator(Model statements, CRContext crContext);
}
