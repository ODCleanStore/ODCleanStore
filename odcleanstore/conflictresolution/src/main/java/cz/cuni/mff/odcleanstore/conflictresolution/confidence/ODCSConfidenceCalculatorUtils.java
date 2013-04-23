package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * @author Jan Michelfeit
 */
/*package*/ final class ODCSConfidenceCalculatorUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSConfidenceCalculatorUtils.class);

    private static final URI sourceScoreProperty = ValueFactoryImpl.getInstance().createURI(ODCS.score);
    private static final URI publisherScoreProperty = ValueFactoryImpl.getInstance().createURI(ODCS.publisherScore);
    private static final URI publisherProperty = ValueFactoryImpl.getInstance().createURI(ODCS.publishedBy);

    public static double sourceConfidence(Resource source, Model metadata, double defaultScore, double publisherScoreWeight) {
        if (source == null) {
            return defaultScore;
        }
        
        Double sourceScore;
        try {
            sourceScore = metadata.filter(source, sourceScoreProperty, null).objectLiteral().doubleValue();
        } catch (Exception e) { // ModelException, NumbefFormatException
            sourceScore = null;
        }

        Double publisherScore = null;
        try {
            Resource publisher = metadata.filter(source, publisherProperty, null).objectResource();
            if (publisher != null) {
                publisherScore = metadata.filter(publisher, publisherScoreProperty, null).objectLiteral().doubleValue();
            }
        } catch (Exception e) { // ModelException, NumbefFormatException
            publisherScore = null;
        }

        if (publisherScore != null && sourceScore != null) {
            return publisherScoreWeight * publisherScore + (1 - publisherScoreWeight) * sourceScore;
        } else if (sourceScore != null) {
            return sourceScore;
        } else if (publisherScore != null) {
            return publisherScore;
        } else {
            LOG.debug("No source or publisher score present, using default score for source {}", source);
            return defaultScore;
        }
    }
    
    private ODCSConfidenceCalculatorUtils() {
    }
}
