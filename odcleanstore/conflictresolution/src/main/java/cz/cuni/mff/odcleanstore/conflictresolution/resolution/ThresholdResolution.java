/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Returns statements with F-quality higher than the given threshold.
 * The thhreshold is given in optional
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
 * {@value #SOURCE_PARAM_NAME}.
 * @author Jan Michelfeit
 */
public class ThresholdResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "THRESHOLD";
    
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
    
    private static final Logger LOG = LoggerFactory.getLogger(ThresholdResolution.class);
    
    /** 
     * Name of the {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
     * specifying the F-quality threshold.
     */
    public static final String THRESHOLD_PARAM_NAME = "threshold";
    
    /** Default threshold value. */
    public static final double DEFAULT_THRESHOLD = 0;
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public ThresholdResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        double threshold = getThresholdParam(crContext.getResolutionStrategy().getParams());
        Collection<Statement> sortedStatements = statements;
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());
        
        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double fQuality = getFQuality(statement.getObject(), sources, crContext);
            if (fQuality > threshold) {
                addResolvedStatement(statement, fQuality, sources, crContext, result);
            }
        }

        return result;
    }

    private void addResolvedStatement(Statement statement, double fQuality, Collection<Resource> sources,
            CRContext crContext, Collection<ResolvedStatement> result) {
        result.add(crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject(),
                fQuality,
                sources));
    }
    
    private double getThresholdParam(Map<String, String> params) {
        String thresholdParam = params.get(THRESHOLD_PARAM_NAME);
        if (thresholdParam == null) {
            return DEFAULT_THRESHOLD;
        }
        double threshold;
        try {
            threshold = Double.parseDouble(thresholdParam);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid value of parameter '{}': {}", THRESHOLD_PARAM_NAME, thresholdParam);
            return DEFAULT_THRESHOLD;
        }
        if (0 < threshold || threshold > 1) {
            LOG.warn("Value of parameter '{}' should be from interval [0;1], {} given", THRESHOLD_PARAM_NAME, threshold);
        }
        return threshold;
    }
}
