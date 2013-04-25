package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;

/**
 * @author Jan Michelfeit
 */
public abstract class MediatingConfidenceCalculator extends ConflictConfidenceCalculator {
    public MediatingConfidenceCalculator(DistanceMeasure distanceMeasure, SourceConfidenceCalculator sourceConfidenceCalculator) {
        super(distanceMeasure, sourceConfidenceCalculator);
    }
    
    public MediatingConfidenceCalculator(DistanceMeasure distanceMeasure) {
        super(distanceMeasure);
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
