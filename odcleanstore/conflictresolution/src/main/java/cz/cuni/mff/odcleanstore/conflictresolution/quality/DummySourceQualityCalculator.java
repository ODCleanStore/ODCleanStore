package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * A dummy implementation of {@link SourceQualityCalculator} returning always {@value #SOURCE_QUALITY}.
 * @author Jan Michelfeit
 */
public class DummySourceQualityCalculator implements SourceQualityCalculator {
    /** The value always returned by {@link #getSourceQuality()}. */
    public static final double SOURCE_QUALITY = 1;
    
    @Override
    public double getSourceQuality(Resource source, Model metadata) {
        return SOURCE_QUALITY;
    }
}