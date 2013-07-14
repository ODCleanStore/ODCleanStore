package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * Implementation of {@link SourceQualityCalculator} suitable for ODCleanStore.
 * The quality is calculated from source named graph quality score (value of property
 * {@value ODCS#score}) and average quality of the corresponding data publisher (property
 * {@value ODCS#publisherScore}. The publisher is expected to be linked to the source 
 * named graph via the {@value ODCS#publishedBy} property. All of these values are obtained
 * from metadata passed to {@link #getSourceQuality(Resource, Model)}. The exact formula if the following:
 * 
 * <code>quality = publisher_quality_weight * publisher_quality_score + 
 *      (1 - publisher_quality_weight) * (&lt;sum of source graph quality scores&gt; / &lt;number of sources&gt;)</code>
 * 
 * @author Jan Michelfeit
 */
public class ODCSSourceQualityCalculator implements SourceQualityCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSSourceQualityCalculator.class);
    private static final URI SOURCE_SCORE_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.score);
    private static final URI PUBLISHER_SCORE_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.publisherScore);
    private static final URI PUBLISHER_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.publishedBy);

    /** Default quality score to be returned when no relevant metadata are available for a source. */ 
    protected final double defaultSourceGraphQuality;

    /** Weight of the publisher quality score as opposed to weight of the source graph score. */
    protected final double publisherQualityWeight;

    /**
     * Creates a new instance.
     * @param defaultSourceGraphQuality default quality score to be returned when no relevant metadata are available for a source
     * @param publisherQualityWeight weight of the publisher quality score as opposed to weight of the source graph score 
     *      (calculated by the Quality Assessment component) in the resulting total source quality score; must
     *      be a number from interval [0,1].
     */
    public ODCSSourceQualityCalculator(double defaultSourceGraphQuality, double publisherQualityWeight) {
        this.publisherQualityWeight = publisherQualityWeight;
        this.defaultSourceGraphQuality = defaultSourceGraphQuality;
    }

    @Override
    public double getSourceQuality(Resource source, Model metadata) {
        if (source == null) {
            return defaultSourceGraphQuality;
        }

        Double sourceScore;
        try {
            sourceScore = metadata.filter(source, SOURCE_SCORE_PROPERTY, null).objectLiteral().doubleValue();
        } catch (Exception e) { // ModelException, NumbefFormatException
            sourceScore = null;
        }

        Double publisherScore = getAveragePublisherScore(metadata, source);

        if (publisherScore != null && sourceScore != null) {
            return publisherQualityWeight * publisherScore + (1 - publisherQualityWeight) * sourceScore;
        } else if (sourceScore != null) {
            return sourceScore;
        } else if (publisherScore != null) {
            return publisherScore;
        } else {
            LOG.debug("No source or publisher score present, using default score for source {}", source);
            return defaultSourceGraphQuality;
        }
    }
    
    private Double getAveragePublisherScore(Model metadata, Resource source) {
        double averageScore = 0;
        int publishersCount = 0;
        
        Model publishers = metadata.filter(source, PUBLISHER_PROPERTY, null);
        for (Statement statement : publishers) {
            try {
                Value publisher = statement.getObject();
                if (!(publisher instanceof Resource)) {
                    continue;
                }
                averageScore += metadata.filter((Resource) publisher, PUBLISHER_SCORE_PROPERTY, null)
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
