package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * Concrete implementation of {@link ConflictFQualityCalculator} suitable
 * for moderating mediating conflict resolution functions (i.e. those producing a value similar to its input values).
 * The base quality (see {@link #valueQuality()}) is determined as the average of quality scores of
 * all value's sources. No other modifications to the quality score as produced by {@link ConflictFQualityCalculator}
 * are made.
 * 
 * @author Jan Michelfeit
 */
public class MediatingModeratingFQualityCalculator extends ConflictFQualityCalculator implements MediatingFQualityCalculator {
    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     * @param distanceMeasure distance measure used to measure the degree of conflict between values
     */
    public MediatingModeratingFQualityCalculator(SourceQualityCalculator sourceQualityCalculator,
            DistanceMeasure distanceMeasure) {
        super(sourceQualityCalculator, distanceMeasure);
    }

    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     */
    public MediatingModeratingFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        super(sourceQualityCalculator);
    }
    
    /**
     * Creates a new instance.
     */
    public MediatingModeratingFQualityCalculator() {
        super();
    }
    
    @Override
    protected double valueQuality(Value value, Collection<Resource> sources, Model metadata) {
        if (sources.size() == 0) {
            return 0;
        }
        double sum = 0;
        for (Resource source : sources) {
            sum += getSourceQuality(source, metadata);
        }
        return sum / sources.size();
    }
}
