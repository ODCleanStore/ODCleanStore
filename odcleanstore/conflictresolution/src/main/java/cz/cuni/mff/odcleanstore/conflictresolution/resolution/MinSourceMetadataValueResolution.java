/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.MetadataValueComparator;

/**
 * Returns the statement with minimum value of the given property of its source.
 * The property is given in required
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
 * {@value #PREDICATE_PARAM_NAME}. The value of the property is then obtained from metadata given
 * in the conflict resolution context.
 * @author Jan Michelfeit
 */
public class MinSourceMetadataValueResolution extends BestSelectedResolutionBase<Resource> {
    private static final Logger LOG = LoggerFactory.getLogger(MinSourceMetadataValueResolution.class);
    private  static final String FUNCTION_NAME = "MIN_SOURCE_METADATA";
    
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
    
    /** Name of parameter specifying the property of source to be compared. */
    public static final String PREDICATE_PARAM_NAME = "predicate";
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public MinSourceMetadataValueResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    protected BestSelectedComparator<Resource> getComparator(Model statements, CRContext crContext) {
        return new ReverseMetadataValueComparator(getPredicateURI(crContext));
    }
    
    @Override
    protected final Resource getComparedValue(Statement statement, CRContext crContext) {
        return statement.getContext();
    }
    
    private URI getPredicateURI(CRContext crContext) {
        String predicate = crContext.getResolutionStrategy().getParams().get(PREDICATE_PARAM_NAME);
        if (predicate == null) {
            LOG.warn("Parameter '{}' missing for resolution function {}, conflicts cannot be resolved",
                    PREDICATE_PARAM_NAME, getClass().getSimpleName());
            return null;
        }

        try {
            return crContext.getResolvedStatementFactory().getValueFactory().createURI(predicate);
        } catch (Exception e) {
            LOG.warn("Value '{}' of parameter '{}' is not a valid URI, conflicts cannot be resolved",
                    predicate, PREDICATE_PARAM_NAME);
            return null;
        }
    }
    
    /** Comparator ordering in the reverse order as {@link MetadataValueComparator}.*/
    private class ReverseMetadataValueComparator extends MetadataValueComparator {
        public ReverseMetadataValueComparator(URI predicateURI) {
            super(predicateURI);
        }
        
        @Override
        public int compare(Resource context1, Resource context2, CRContext crContext) {
            return super.compare(context1, context2, crContext);
        }
    }

}