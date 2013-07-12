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
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * Returns statement with object which is the most often occurring object value, weighted by quality scores of 
 * the respective sources. 
 * @author Jan Michelfeit
 */
public class WeightedVoteResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "WEIGHTED_VOTE";
    
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
    
    private final SourceQualityCalculator sourceQualityCalculator;
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of
     *        produced {@link ResolvedStatement result quads} (see
     *        {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator})
     * @param sourceQualityCalculator calculator of quality score of quad source named graphs
     */
    public WeightedVoteResolution(DecidingFQualityCalculator fQualityCalculator, 
            SourceQualityCalculator sourceQualityCalculator) {
        super(fQualityCalculator);
        this.sourceQualityCalculator = sourceQualityCalculator;
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
            votes += sourceQualityCalculator.getSourceQuality(statement.getContext(), crContext.getMetadata());
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
        double quality = getFQuality(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                quality,
                sources);
        return Collections.singleton(resolvedStatement);
    }
}
