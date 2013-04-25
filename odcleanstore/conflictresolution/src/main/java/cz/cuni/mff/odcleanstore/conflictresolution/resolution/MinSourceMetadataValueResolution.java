/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class MinSourceMetadataValueResolution extends MaxSourceMetadataValueResolution {
    public static final String PREDICATE_PARAM_NAME = "predicate";
    
    public MinSourceMetadataValueResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected int compare(Statement statement1, Statement statement2, URI predicateURI, CRContext crContext) {
        return -super.compare(statement1, statement2, predicateURI, crContext); 
    }
}
