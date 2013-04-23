/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;


/**
 * @author Jan Michelfeit
 */
public interface ResolutionStrategy {
    String getResolutionFunctionName();
    
    EnumCardinality getCardinality(); // TODO - rename?
    
    EnumAggregationErrorStrategy getAggregationErrorStrategy();
    
    Map<String, String> getParams();
}
