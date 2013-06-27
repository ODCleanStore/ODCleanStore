package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * @author Jan Michelfeit
 */
public interface SourceQualityCalculator  {
    double getSourceQuality(Resource source, Model metadata);
}