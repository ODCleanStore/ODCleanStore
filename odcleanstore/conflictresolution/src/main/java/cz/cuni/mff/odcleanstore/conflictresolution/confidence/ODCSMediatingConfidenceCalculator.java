/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;

/**
 * @author Jan Michelfeit
 */
public class ODCSMediatingConfidenceCalculator extends MediatingConfidenceCalculator {
    protected final double defaultScore;
    protected final double publisherScoreWeight;

    public ODCSMediatingConfidenceCalculator(DistanceMeasure distanceMeasure, double defaultScore, double publisherScoreWeight) {
        super(distanceMeasure);
        this.publisherScoreWeight = publisherScoreWeight;
        this.defaultScore = defaultScore;
    }

    @Override
    protected double sourceConfidence(Resource source, Model metadata) {
        return ODCSConfidenceCalculatorUtils.sourceConfidence(source, metadata, defaultScore, publisherScoreWeight);
    }
}

