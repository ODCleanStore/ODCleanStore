package cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.MediatingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class MediatingConflictConfidenceCalculator extends ConflictConfidenceCalculator implements MediatingConfidenceCalculator {
    public MediatingConflictConfidenceCalculator(SourceConfidenceCalculator sourceConfidenceCalculator, DistanceMeasure distanceMeasure) {
        super(sourceConfidenceCalculator, distanceMeasure);
    }
    
    public MediatingConflictConfidenceCalculator(SourceConfidenceCalculator sourceConfidenceCalculator) {
        super(sourceConfidenceCalculator);
    }
    
    public MediatingConflictConfidenceCalculator() {
        super();
    }
    
    @Override
    protected double valueConfidence(Value value, Collection<Resource> sources, Model metadata) {
        double sum = 0;
        for (Resource source : sources) {
            sum += sourceConfidence(source, metadata);
        }
        return sum / sources.size();
    }
}
