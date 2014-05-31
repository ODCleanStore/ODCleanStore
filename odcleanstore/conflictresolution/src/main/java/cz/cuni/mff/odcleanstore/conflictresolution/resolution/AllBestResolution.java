/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.ObjectComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Resolution function which selects the quad(s) with the highest F-Quality score. 
 * If there is more than one such value, returns all of values with the highest score. 
 * @author Jan Michelfeit
 */
public class AllBestResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "ALLBEST";
    
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
    
    /** Instance of comparator by objects. */
    protected static final Comparator<Statement> OBJECT_COMPARATOR = new ObjectComparator();
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public AllBestResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    } 
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        Collection<Statement> sortedStatements = statements;

        Collection<ResolvedStatement> bestStatements = new ArrayList<ResolvedStatement>();
        double bestQuality = Double.NEGATIVE_INFINITY;

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double quality = getFQuality(statement.getObject(), sources, crContext);
            if (quality > bestQuality) {
                bestQuality = quality;
                bestStatements.clear();
                bestStatements.add(crContext.getResolvedStatementFactory().create(statement, bestQuality, sources));
            } else if (quality == bestQuality) {
                bestStatements.add(crContext.getResolvedStatementFactory().create(statement, bestQuality, sources));
            }
        }

        return bestStatements;
    }
}
