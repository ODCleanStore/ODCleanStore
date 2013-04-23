/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Model;


/**
 * @author Jan Michelfeit
 */
public interface ResolutionFunction { // TODO ResolutionFunction only?   
    Collection<ResolvedStatement> resolve(Model statements, CRContext context);
}
