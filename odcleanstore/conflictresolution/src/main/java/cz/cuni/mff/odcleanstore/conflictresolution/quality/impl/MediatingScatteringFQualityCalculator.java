/**
 * 
 */
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
 * @author Jan Michelfeit
 */
public class MediatingScatteringFQualityCalculator implements FQualityCalculator, MediatingFQualityCalculator {
    private final SourceQualityCalculator sourceQualityCalculator;
    
    public MediatingScatteringFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        this.sourceQualityCalculator = sourceQualityCalculator; 
    }
    
    public MediatingScatteringFQualityCalculator() {
        this(new DummySourceQualityCalculator());
    }
    
    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
        double sum = 0;
        for (Resource source : sources) {
            sum += sourceQualityCalculator.getSourceQuality(source, context.getMetadata());
        }
        return sum / sources.size();
    }
}
