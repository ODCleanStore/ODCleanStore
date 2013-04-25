/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;

/**
 * @author Jan Michelfeit
 */
public class WeightedVoteResolution extends DecidingResolutionFunction {
    private final SourceConfidenceCalculator sourceConfidenceCalculator;
    
    public WeightedVoteResolution(ConfidenceCalculator confidenceCalculator, SourceConfidenceCalculator sourceConfidenceCalculator) {
        super(confidenceCalculator);
        this.sourceConfidenceCalculator = sourceConfidenceCalculator;
    } 
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<Statement> sortedStatements = statements;

        Statement bestStatement = null;
        double mostVotes = -1;

        // cluster means a sequence of statements with the same object
        Statement lastStatement = null;
        double votes = 0;
        for (Statement statement : sortedStatements) {
            if (bestStatement != null && !CRUtils.sameValues(statement.getObject(), lastStatement.getObject())) {
                // beginning of new cluster, consider previous for result
                if (votes > mostVotes) {
                    bestStatement = lastStatement;
                    mostVotes = votes;
                }
                votes = 0;
            }
            votes += sourceConfidenceCalculator.sourceConfidence(statement.getContext(), crContext.getMetadata());
            lastStatement = statement;
        }
        if (votes > mostVotes) {
            // don't forget last cluster
            bestStatement = lastStatement;
        }
        
        if (bestStatement == null) {
            return Collections.emptySet();
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double confidence = getConfidence(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                confidence,
                sources);
        return Collections.singleton(resolvedStatement);
    }
}
