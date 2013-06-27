/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * @author Jan Michelfeit
 */
public interface FQualityCalculator {
    double getFQuality(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext crContext); // sources as URIs? 
}
