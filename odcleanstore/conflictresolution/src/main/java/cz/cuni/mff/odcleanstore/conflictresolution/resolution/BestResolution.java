/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

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
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;

/**
 * @author Jan Michelfeit
 */
public class BestResolution extends DecidingResolutionFunction {
    protected static final Comparator<Statement> OBJECT_COMPARATOR = new ObjectComparator();
    
    protected BestResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    } 
    
    // TODO:
    // if (quality > bestQuadQuality
    // || (quality == bestQuadQuality && AggregationUtils.compareByInsertedAt(lastQuad, bestQuad, metadata) > 0)) {

    // TODO: guaranteed ordering & unique?
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<Statement> sortedStatements = statements;
        // Shouldn't be necessary since statements are guaranteed to be ordered by spog:
        // if (!ResolutionFunctionUtils.isSorted(sortedStatements, OBJECT_COMPARATOR)) {
        // Statement[] statementArr = sortedStatements.toArray(new Statement[0]);
        // Arrays.sort(statementArr, OBJECT_COMPARATOR);
        // sortedStatements = Arrays.asList(statementArr);
        // } 

        Statement bestStatement = null;
        Collection<Resource> bestStatementSources = null;
        double bestConfidence = Double.NEGATIVE_INFINITY;

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double confidence = getConfidence(statement.getObject(), statements, sources, crContext);
            if (confidence > bestConfidence) {
                bestStatement = statement;
                bestConfidence = confidence;
                bestStatementSources = sources;
            }
        }

        if (bestStatement == null) {
            return Collections.emptySet();
        }

        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                bestConfidence,
                bestStatementSources);
        return Collections.singleton(resolvedStatement);
    }
}
