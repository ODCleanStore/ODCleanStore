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
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.ObjectComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;

/**
 * @author Jan Michelfeit
 */
public class BestResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "BEST";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    protected static final Comparator<Statement> OBJECT_COMPARATOR = new ObjectComparator();
    
    public BestResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    } 
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<Statement> sortedStatements = statements;

        Statement bestStatement = null;
        Collection<Resource> bestStatementSources = null;
        double bestQuality = Double.NEGATIVE_INFINITY;

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double quality = getFQuality(statement.getObject(), statements, sources, crContext);
            if (quality > bestQuality) {
                bestStatement = statement;
                bestQuality = quality;
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
                bestQuality,
                bestStatementSources);
        return Collections.singleton(resolvedStatement);
    }
}
