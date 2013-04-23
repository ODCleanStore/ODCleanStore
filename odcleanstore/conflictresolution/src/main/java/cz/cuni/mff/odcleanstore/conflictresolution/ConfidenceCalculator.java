/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 * @author Jan Michelfeit
 */
public interface ConfidenceCalculator {
    double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext crContext); // sources as URIs? 
}
