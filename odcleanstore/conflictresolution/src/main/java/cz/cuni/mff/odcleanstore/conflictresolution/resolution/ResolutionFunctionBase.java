/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;

/**
 * @author Jan Michelfeit
 */
public abstract class ResolutionFunctionBase implements ResolutionFunction {
    private static final Logger LOG = LoggerFactory.getLogger(ResolutionFunctionBase.class);
    
    protected final ConfidenceCalculator confidenceCalculator;

    protected ResolutionFunctionBase(ConfidenceCalculator confidenceCalculator) {
        if (confidenceCalculator == null) {
            throw new IllegalArgumentException();
        }
        this.confidenceCalculator = confidenceCalculator;
    }
    
    protected ConfidenceCalculator getConfidenceCalculator() {
        return confidenceCalculator;
    }
    
    protected double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext crContext) {
        return confidenceCalculator.getConfidence(value, conflictingStatements, sources, crContext);
    }
    
    protected final Set<Resource> filterSources(Statement statement, Model conflictingStatements) {
        Iterator<Statement> matchingStatements = conflictingStatements.filter(
                statement.getSubject(), 
                statement.getPredicate(), 
                statement.getObject()).iterator();
        
        if (!matchingStatements.hasNext()) {
            return Collections.emptySet();
        }
        Resource firstSource = matchingStatements.next().getContext();
        if (!matchingStatements.hasNext()) {
            return Collections.singleton(firstSource);
        }
        
        Set<Resource> sources = new ArrayListSet<Resource>(4); // We can use array list, because statements in Model are unique
        sources.add(firstSource);
        while (matchingStatements.hasNext()) {
            sources.add(matchingStatements.next().getContext());
        }
        return sources;
    }
    
    protected void handleNonAggregableStatement(
            Statement nonAggregableStatement,
            Model conflictingStatements,
            CRContext crContext,
            Collection<ResolvedStatement> result) {

        LOG.debug("Value {} cannot be aggregated with {}.",
                nonAggregableStatement.getObject(), getClass().getSimpleName());

        switch (crContext.getResolutionStrategy().getAggregationErrorStrategy()) {
        case RETURN_ALL:
            Collection<Resource> sources = filterSources(nonAggregableStatement, conflictingStatements);
            double confidence = getConfidence(
                    nonAggregableStatement.getObject(), 
                    conflictingStatements, 
                    sources, 
                    crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    nonAggregableStatement.getSubject(), 
                    nonAggregableStatement.getPredicate(),
                    nonAggregableStatement.getObject(),
                    confidence,
                    sources);
            result.add(resolvedStatement);
            break;
        case IGNORE:
            return;
        default:
            LOG.warn("Unhandled aggregation error strategy {}", crContext.getResolutionStrategy().getAggregationErrorStrategy());
        }
    }
    
    private class ArrayListSet<T> extends ArrayList<T> implements Set<T> {
        private static final long serialVersionUID = 1L;

        /**
        * Constructs an empty list with the specified initial capacity.
        *
        * @param  initialCapacity  the initial capacity of the list
        */
       public ArrayListSet(int initialCapacity) {
           super(initialCapacity);
       }
    }
}
