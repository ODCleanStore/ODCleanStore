package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.ObjectComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Resolution function returning all input quads.
 * In effect the aggregation doesn't do anything except for adding a quality estimate.
 *
 * @author Jan Michelfeit
 */
public class NoneResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "NONE";
    
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
    
    /** Comparator by objects. */
    protected static final Comparator<Statement> OBJECT_COMPARATOR = new ObjectComparator();
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public NoneResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    } 
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>();
        
        for (Statement statement : statements) {
            Collection<Resource> source = Collections.singleton(statement.getContext());
            double fQuality = getFQuality(
                    statement.getObject(),
                    source,
                    crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    statement.getSubject(), 
                    statement.getPredicate(),
                    statement.getObject(),
                    fQuality,
                    source);
            result.add(resolvedStatement);
        }
        return result;
    }
}
