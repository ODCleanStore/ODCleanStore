/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DummySourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.MediatingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class SimpleMediatingConfidenceCalculator implements ConfidenceCalculator, MediatingConfidenceCalculator {
    private final SourceConfidenceCalculator sourceConfidenceCalculator;
    
    public SimpleMediatingConfidenceCalculator(SourceConfidenceCalculator sourceConfidenceCalculator) {
        this.sourceConfidenceCalculator = sourceConfidenceCalculator; 
    }
    
    public SimpleMediatingConfidenceCalculator() {
        this(new DummySourceConfidenceCalculator());
    }
    
    @Override
    public double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
        double sum = 0;
        for (Resource source : sources) {
            sum += sourceConfidenceCalculator.sourceConfidence(source, context.getMetadata());
        }
        return sum / sources.size();
    }
}
