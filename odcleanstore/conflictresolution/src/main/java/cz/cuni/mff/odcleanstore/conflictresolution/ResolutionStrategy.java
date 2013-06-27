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
    
    EnumCardinality getCardinality();
    
    EnumAggregationErrorStrategy getAggregationErrorStrategy();
    
    Map<String, String> getParams();
}
