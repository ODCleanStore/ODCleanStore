/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.comparators.ObjectComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;

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
        Iterator<Statement> clusterStart = sortedStatements.iterator();
        Statement lastStatement = null;
        int clusterSize = 0;
        for (Statement statement : sortedStatements) {
            if (clusterSize > 0 && !CRUtils.sameValues(statement.getObject(), lastStatement.getObject())) {
                // beginning of new cluster, add the previous to the result
                Collection<Resource> sources = getSources(clusterStart, clusterSize);
                double confidence = getConfidence(lastStatement.getObject(), statements, sources, crContext);
                if (confidence > bestConfidence) {
                    bestStatement = lastStatement;
                    bestConfidence = confidence;
                    bestStatementSources = sources;
                }
                clusterSize = 0;
            }

            clusterSize++;
            lastStatement = statement;
        }
        if (clusterSize > 0) {
            // don't forget last cluster
            Collection<Resource> sources = getSources(clusterStart, clusterSize);
            double confidence = getConfidence(lastStatement.getObject(), statements, sources, crContext);
            if (confidence > bestConfidence) {
                bestStatement = lastStatement;
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

    private Collection<Resource> getSources(Iterator<Statement> clusterIt, int clusterSize) {
        if (clusterSize == 1) {
            return Collections.singleton(clusterIt.next().getContext());
        } else {
            Collection<Resource> sources = new ArrayList<Resource>(clusterSize);
            for (int i = 0; i < clusterSize; i++) {
                sources.add(clusterIt.next().getContext());
            }
            return sources;
        }
    }
}
