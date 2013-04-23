/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class ODCSDecidingConfidenceCalculator extends DecidingConfidenceCalculator {
    protected final double defaultScore;
    protected final double publisherScoreWeight;

    public ODCSDecidingConfidenceCalculator(DistanceMeasure distanceMeasure, double defaultScore, double publisherScoreWeight, double agreeCoeficient) {
        super(distanceMeasure, agreeCoeficient);
        this.publisherScoreWeight = publisherScoreWeight;
        this.defaultScore = defaultScore;
    }

    @Override
    protected double sourceConfidence(Resource source, Model metadata) {
        return ODCSConfidenceCalculatorUtils.sourceConfidence(source, metadata, defaultScore, publisherScoreWeight);
    }
}
