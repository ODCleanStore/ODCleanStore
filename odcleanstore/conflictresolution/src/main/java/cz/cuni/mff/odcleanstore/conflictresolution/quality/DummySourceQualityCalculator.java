package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * @author Jan Michelfeit
 */
public class DummySourceQualityCalculator implements SourceQualityCalculator {
    public static final double SOURCE_QUALITY = 1;
    
    @Override
    public double getSourceQuality(Resource source, Model metadata) {
        return SOURCE_QUALITY;
    }
}