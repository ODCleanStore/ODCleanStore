/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class SimpleMediatingConfidenceCalculator implements ConfidenceCalculator {
    protected abstract double sourceConfidence(Resource source, Model metadata);
    
    @Override
    public double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
        double sum = 0;
        for (Resource source : sources) {
            sum += sourceConfidence(source, context.getMetadata());
        }
        return sum / sources.size();
    }
}
