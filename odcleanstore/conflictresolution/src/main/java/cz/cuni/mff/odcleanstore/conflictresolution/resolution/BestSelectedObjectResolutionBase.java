/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class BestSelectedObjectResolutionBase extends BestSelectedResolutionBase<Value> {
    protected BestSelectedObjectResolutionBase(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }
    
    @Override
    protected Value getComparedValue(Statement statement, CRContext crContext) {
        return statement.getObject();
    }
}
