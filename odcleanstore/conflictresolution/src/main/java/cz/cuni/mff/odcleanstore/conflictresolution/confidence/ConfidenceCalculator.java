/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * @author Jan Michelfeit
 */
public interface ConfidenceCalculator {
    double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext crContext); // sources as URIs? 
}
