/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;

/**
 * @author Jan Michelfeit
 */
public class TresholdResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "TRESHOLD";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(TresholdResolution.class);
    public static final String TRESHOLD_PARAM_NAME = "treshold";
    public static final double DEFAULT_TRESHOLD = 0;
    
    public TresholdResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        double treshold = getTresholdParam(crContext.getResolutionStrategy().getParams());
        Collection<Statement> sortedStatements = statements;
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());
        
        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double confidence = getConfidence(statement.getObject(), statements, sources, crContext);
            if (confidence > treshold) {
                addResolvedStatement(statement, confidence, sources, crContext, result);
            }
        }

        return result;
    }

    private void addResolvedStatement(Statement statement, double confidence, Collection<Resource> sources,
            CRContext crContext, Collection<ResolvedStatement> result) {
        result.add(crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject(),
                confidence,
                sources));
    }
    
    private double getTresholdParam(Map<String, String> params) {
        String tresholdParam = params.get(TRESHOLD_PARAM_NAME);
        if (tresholdParam == null) {
            return DEFAULT_TRESHOLD;
        }
        double treshold;
        try {
            treshold = Double.parseDouble(tresholdParam);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid value of parameter '{}': {}", TRESHOLD_PARAM_NAME, tresholdParam);
            return DEFAULT_TRESHOLD;
        }
        if (0 < treshold || treshold > 1) {
            LOG.warn("Value of parameter '{}' should be from interval [0;1], {} given", TRESHOLD_PARAM_NAME, treshold);
        }
        return treshold;
    }
}
