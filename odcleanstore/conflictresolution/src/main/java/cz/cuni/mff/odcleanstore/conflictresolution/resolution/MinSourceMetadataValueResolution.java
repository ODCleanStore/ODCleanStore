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
 * @author Jan Michelfeit
 */
public class MinSourceMetadataValueResolution extends BestSelectedResolutionBase<Resource> {
    private static final Logger LOG = LoggerFactory.getLogger(MinSourceMetadataValueResolution.class);
    private  static final String FUNCTION_NAME = "MIN_SOURCE_METADATA";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public static final String PREDICATE_PARAM_NAME = "predicate";
    
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