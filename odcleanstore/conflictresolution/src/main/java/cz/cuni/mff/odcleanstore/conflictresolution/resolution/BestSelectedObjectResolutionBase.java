/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class BestSelectedObjectResolutionBase extends BestSelectedResolutionBase<Value> {
    protected BestSelectedObjectResolutionBase(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
    
    @Override
    protected Value getComparedValue(Statement statement, CRContext crContext) {
        return statement.getObject();
    }
}
