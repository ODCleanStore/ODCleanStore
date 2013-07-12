/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.ODCSInsertedAtComparator;

/**
 * Returns the statement with the latest insertion date to ODCleanStore.
 * @see {@link ODCSInsertedAtComparator}
 * @author Jan Michelfeit
 */
public class ODCSLatestResolution extends BestSelectedResolutionBase<Resource> {
    private  static final String FUNCTION_NAME = "LATEST";
    
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
    
    private static final BestSelectedComparator<Resource> COMPARATOR = new ODCSInsertedAtComparator();

    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public ODCSLatestResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    protected BestSelectedComparator<Resource> getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }

    @Override
    protected Resource getComparedValue(Statement statement, CRContext crContext) {
        return statement.getContext();
    }
}
