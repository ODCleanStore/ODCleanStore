package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * A dummy implementation of {@link SourceQualityCalculator} returning a constant value as source quality.
 * @author Jan Michelfeit
 */
public class DummySourceQualityCalculator implements SourceQualityCalculator {
    /** The default returned value by {@link #getSourceQuality(org.openrdf.model.Resource, org.openrdf.model.Model)}. */
    public static final double DEFAULT_SOURCE_QUALITY = 0.5;
    
    private final double sourceQuality;
    
    /** 
     * Creates a new instance returning the default source quality {@value #DEFAULT_SOURCE_QUALITY}. 
     */
    public DummySourceQualityCalculator() {
        sourceQuality = DEFAULT_SOURCE_QUALITY;
    }
    
    /**
     * Creates a new instance returning the give source quality.
     * @param sourceQuality the constant value to return as source quality
     */
    public DummySourceQualityCalculator(double sourceQuality) {
        this.sourceQuality = sourceQuality;
    }
    
    @Override
    public double getSourceQuality(Resource source, Model metadata) {
        return sourceQuality;
    }
}