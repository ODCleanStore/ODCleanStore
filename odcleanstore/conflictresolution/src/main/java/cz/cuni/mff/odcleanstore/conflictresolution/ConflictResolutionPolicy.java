/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;

import org.openrdf.model.URI;


/**
 * @author Jan Michelfeit
 */
public interface ConflictResolutionPolicy {
    ResolutionStrategy getDefaultResolutionStrategy();
    
    Map<URI, ResolutionStrategy> getPropertyResolutionStrategies();
}
