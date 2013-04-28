/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;


/**
 * @author Jan Michelfeit
 */
public interface ResolutionFunction { 
    Collection<ResolvedStatement> resolve(Model statements, CRContext context) throws ConflictResolutionException;
}
