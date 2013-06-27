package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public class DecidingConflictFQualityCalculator extends ConflictFQualityCalculator implements DecidingFQualityCalculator {
    public static final double DEFAULT_AGREE_COEFICIENT = 4;
    protected final double agreeCoeficient;
    
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, double agreeCoeficient,  DistanceMeasure distanceMeasure) {
        super(sourceQualityCalculator, distanceMeasure);
        this.agreeCoeficient = agreeCoeficient;
    }
    
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, double agreeCoeficient) {
        super(sourceQualityCalculator);
        this.agreeCoeficient = agreeCoeficient;
    }
    
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        super(sourceQualityCalculator);
        this.agreeCoeficient = DEFAULT_AGREE_COEFICIENT;
    }
    
    public DecidingConflictFQualityCalculator() {
        super();
        this.agreeCoeficient = DEFAULT_AGREE_COEFICIENT;
    }
    
    @Override
    protected double valueQuality(Value value, Collection<Resource> sources, Model metadata) {
        double max = 0;
        for (Resource source : sources) {
            double sourceQuality = getSourceQuality(source, metadata);
            if (sourceQuality > max) {
                max = sourceQuality;
            }
        }
        return max;
    }
    
    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
        double quality = super.getFQuality(value, conflictingStatements, sources, context);

         // Increase score if multiple sources agree on the result value
         if (sources.size() > 1) {
             Model metadata = context.getMetadata();
             double sourcesQualitySum = 0;
             double maxSourceQuality = 0;
             for (Resource source : sources) {
                 double sourceQuality = getSourceQuality(source, metadata);
                 sourcesQualitySum += sourceQuality;
                 if (sourceQuality > maxSourceQuality) {
                     maxSourceQuality = sourceQuality;
                 }
             }
             double supportFactor = (sourcesQualitySum - maxSourceQuality) / agreeCoeficient;
             if (supportFactor > 1) {
                 supportFactor = 1;
             } else if (supportFactor < 0) {
                 supportFactor = 0;
             }
             quality += (1 - quality) * supportFactor;
         }

         return quality;
    }
}
