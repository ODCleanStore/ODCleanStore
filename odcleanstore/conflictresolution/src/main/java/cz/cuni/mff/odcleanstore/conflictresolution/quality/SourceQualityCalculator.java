package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

/**
 * Calculator of graph quality scores.
 * Graph quality score is a quality of a data source modeled on the named graph level. 
 * @author Jan Michelfeit
 */
public interface SourceQualityCalculator  {
    /** 
     * Returns the graph quality score for a named graph identified by source.
     * @param source URI of the source named graph
     * @param metadata metadata which can be used for the calculation
     * @return graph quality score of source
     */
    double getSourceQuality(Resource source, Model metadata);
}