package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DummySourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * Implementation of {@link FQualityCalculator} suitable
 * for scattering mediating conflict resolution functions (i.e. those producing values not similar to its input values).
 * The result F-quality is determined as the average of quality scores of all sources.
 * 
 * @author Jan Michelfeit
 */
public class MediatingScatteringFQualityCalculator implements FQualityCalculator, MediatingFQualityCalculator {
    private final SourceQualityCalculator sourceQualityCalculator;
    
    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     */
    public MediatingScatteringFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        this.sourceQualityCalculator = sourceQualityCalculator; 
    }
    
    /**
     * Creates a new instance.
     */
    public MediatingScatteringFQualityCalculator() {
        this(new DummySourceQualityCalculator());
    }
    
    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, 
            Collection<Resource> sources, CRContext context) {
        
        if (sources.size() == 0) {
            return 0;
        }
        double sum = 0;
        for (Resource source : sources) {
            sum += sourceQualityCalculator.getSourceQuality(source, context.getMetadata());
        }
        return sum / sources.size();
    }
}
