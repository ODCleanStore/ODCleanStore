package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.ConflictConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class DecidingConfidenceCalculator extends ConflictConfidenceCalculator {
    protected final double agreeCoeficient;
    
    public DecidingConfidenceCalculator(DistanceMeasure distanceMeasure, double agreeCoeficient) {
        super(distanceMeasure);
        this.agreeCoeficient = agreeCoeficient;
    }
    
    @Override
    protected double valueConfidence(Value value, Collection<Resource> sources, Model metadata) {
        double max = 0;
        for (Resource source : sources) {
            double sourceConfidence = sourceConfidence(source, metadata);
            if (sourceConfidence > max) {
                max = sourceConfidence;
            }
        }
        return max;
    }
    
    @Override
    public double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
        double confidence = super.getConfidence(value, conflictingStatements, sources, context);

         // Increase score if multiple sources agree on the result value
         if (sources.size() > 1) {
             Model metadata = context.getMetadata();
             double sourcesConfidenceSum = 0;
             for (Resource source :sources) {
                 sourcesConfidenceSum += sourceConfidence(source, metadata);
             }
             double agreeCoef = (sourcesConfidenceSum - confidence) / agreeCoeficient;
             if (agreeCoef > 1) {
                 agreeCoef = 1;
             } else if (agreeCoef < 0) {
                 agreeCoef = 0;
             }
             confidence += (1 - confidence) * agreeCoef;
         }

         return confidence;
    }
}
