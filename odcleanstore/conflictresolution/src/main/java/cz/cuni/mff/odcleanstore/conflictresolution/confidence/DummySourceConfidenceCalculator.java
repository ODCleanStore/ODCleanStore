package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * @author Jan Michelfeit
 */
public class DummySourceConfidenceCalculator implements SourceConfidenceCalculator {
    public static final double SOURCE_CONFIDENCE = 1;
    
    @Override
    public double sourceConfidence(Resource source, Model metadata) {
        return SOURCE_CONFIDENCE;
    }
}