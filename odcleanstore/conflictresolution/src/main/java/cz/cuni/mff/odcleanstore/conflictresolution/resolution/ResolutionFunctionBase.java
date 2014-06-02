/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.SmallContextsSet;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Base class for conflict resolution function implementations.
 * @author Jan Michelfeit
 */
public abstract class ResolutionFunctionBase implements ResolutionFunction {
    private static final Logger LOG = LoggerFactory.getLogger(ResolutionFunctionBase.class);
    
    /** The {@link FQualityCalculator F-Quality calculator} to be used. */
    protected final FQualityCalculator fQualityCalculator;

    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    protected ResolutionFunctionBase(FQualityCalculator fQualityCalculator) {
        if (fQualityCalculator == null) {
            throw new IllegalArgumentException();
        }
        this.fQualityCalculator = fQualityCalculator;
    }
    
    // /**
    // * Returns the F-quality calculator to be used.
    // * @return an F-quality calculator
    // */
    // protected FQualityCalculator getFQualityCalculator() {
    // return fQualityCalculator;
    // }
    
    /**
     * Returns the F-quality for the given value in the current conflict resolution context. 
     * @param value estimated value - object of the statement to be estimated
     * @param sources collection of sources (their named graph URIs) claiming the given value
     * @param crContext context of conflict resolution
     * @return F-quality of <code>value</code> (or its corresponding {@link ResolvedStatement resolved quad}, respectively)
     */
    protected double getFQuality(Value value,
            Collection<Resource> sources, CRContext crContext) {

        return fQualityCalculator.getFQuality(value, crContext.getConflictingStatements(), sources, crContext);
    }
    
    /**
     * Returns set of name graph URIs of all statements in conflictingStatements that share
     * the same subject, predicate and object as statement.
     * @param statement searched statement
     * @param conflictingStatements (potentially) conflicting statements
     * @return  set of source name graph URIs for the given statement
     */
    protected final Set<Resource> filterSources(Statement statement, Model conflictingStatements) {
        Iterator<Statement> matchingStatements = conflictingStatements.filter(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject()).iterator();
        return SmallContextsSet.fromIterator(matchingStatements);
    }

    /**
     * Handle a statement which cannot be aggregated by the chosen resolution function, depending on
     * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getAggregationErrorStrategy()}.
     * @param nonAggregableStatement the statement to be handled
     * @param statements potentially conflicting statements
     * @param crContext context of conflict resolution
     * @param result result container where the statement can be added to
     */
    protected void handleNonAggregableStatement(
            Statement nonAggregableStatement,
            Model statements,
            CRContext crContext,
            Collection<ResolvedStatement> result) {

        LOG.debug("Value {} cannot be aggregated with {}.",
                nonAggregableStatement.getObject(), getClass().getSimpleName());

        switch (crContext.getResolutionStrategy().getAggregationErrorStrategy()) {
        case RETURN_ALL:
            Collection<Resource> sources = filterSources(nonAggregableStatement, statements);
            double fQuality = getFQuality(
                    nonAggregableStatement.getObject(),
                    sources,
                    crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    nonAggregableStatement.getSubject(), 
                    nonAggregableStatement.getPredicate(),
                    nonAggregableStatement.getObject(),
                    fQuality,
                    sources);
            result.add(resolvedStatement);
            break;
        case IGNORE:
            return;
        default:
            LOG.warn("Unhandled aggregation error strategy {}", crContext.getResolutionStrategy().getAggregationErrorStrategy());
        }
    }
}
