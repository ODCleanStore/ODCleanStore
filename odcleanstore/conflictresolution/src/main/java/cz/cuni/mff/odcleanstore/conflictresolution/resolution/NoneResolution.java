package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.comparators.ObjectComparator;

/**
 * Aggregation method that returns all input triples unchanged.
 * In effect the aggregation doesn't do anything except for adding a quality estimate.
 *
 * @author Jan Michelfeit
 */
public class NoneResolution extends DecidingResolutionFunction {
    protected static final Comparator<Statement> OBJECT_COMPARATOR = new ObjectComparator();
    
    protected NoneResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    } 
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>();
        
        for (Statement statement : statements) {
            Collection<Resource> source = Collections.singleton(statement.getContext());
            double confidence = getConfidence(
                    statement.getObject(), 
                    statements, 
                    source, 
                    crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    statement.getSubject(), 
                    statement.getPredicate(),
                    statement.getObject(),
                    confidence,
                    source);
            result.add(resolvedStatement);
        }
        return result;
    }
}
