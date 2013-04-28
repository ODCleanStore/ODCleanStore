package cz.cuni.mff.odcleanstore.conflictresolution.confidence.impl;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * @author Jan Michelfeit
 */
public class ODCSSourceConfidenceCalculator implements SourceConfidenceCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSSourceConfidenceCalculator.class);
    private static final URI sourceScoreProperty = ValueFactoryImpl.getInstance().createURI(ODCS.score);
    private static final URI publisherScoreProperty = ValueFactoryImpl.getInstance().createURI(ODCS.publisherScore);
    private static final URI publisherProperty = ValueFactoryImpl.getInstance().createURI(ODCS.publishedBy);

    protected final double defaultScore;
    protected final double publisherScoreWeight;

    public ODCSSourceConfidenceCalculator(double defaultScore, double publisherScoreWeight) {
        this.publisherScoreWeight = publisherScoreWeight;
        this.defaultScore = defaultScore;
    }

    @Override
    public double sourceConfidence(Resource source, Model metadata) {
        if (source == null) {
            return defaultScore;
        }

        Double sourceScore;
        try {
            sourceScore = metadata.filter(source, sourceScoreProperty, null).objectLiteral().doubleValue();
        } catch (Exception e) { // ModelException, NumbefFormatException
            sourceScore = null;
        }

        Double publisherScore = getAveragePublisherScore(metadata, source);

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
    
    private Double getAveragePublisherScore(Model metadata, Resource source) {
        double averageScore = 0;
        int publishersCount = 0;
        
        Model publishers = metadata.filter(source, publisherProperty, null);
        for (Statement statement : publishers) {
            try {
                Value publisher = statement.getObject();
                if (!(publisher instanceof Resource)) {
                    continue;
                }
                averageScore += metadata.filter((Resource) publisher, publisherScoreProperty, null)
                        .objectLiteral().doubleValue();
                publishersCount++;
            } catch (Exception e) { // ModelException, NumbefFormatException
                continue;
            }
        }

        return publishersCount > 0
                ? averageScore / publishersCount
                : null;
    }
}
