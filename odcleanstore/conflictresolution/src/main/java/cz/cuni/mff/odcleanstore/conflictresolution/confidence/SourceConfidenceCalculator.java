package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * @author Jan Michelfeit
 */
public interface SourceConfidenceCalculator  {
    double sourceConfidence(Resource source, Model metadata);
}