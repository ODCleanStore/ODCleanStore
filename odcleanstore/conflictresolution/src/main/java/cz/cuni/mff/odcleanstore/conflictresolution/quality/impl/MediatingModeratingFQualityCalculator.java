package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public class MediatingModeratingFQualityCalculator extends ConflictFQualityCalculator implements MediatingFQualityCalculator {
    public MediatingModeratingFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, DistanceMeasure distanceMeasure) {
        super(sourceQualityCalculator, distanceMeasure);
    }
    
    public MediatingModeratingFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        super(sourceQualityCalculator);
    }
    
    public MediatingModeratingFQualityCalculator() {
        super();
    }
    
    @Override
    protected double valueQuality(Value value, Collection<Resource> sources, Model metadata) {
        double sum = 0;
        for (Resource source : sources) {
            sum += getSourceQuality(source, metadata);
        }
        return sum / sources.size();
    }
}
