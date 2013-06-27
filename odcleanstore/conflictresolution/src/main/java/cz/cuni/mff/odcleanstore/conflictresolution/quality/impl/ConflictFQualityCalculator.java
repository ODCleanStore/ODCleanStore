/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.quality.impl;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.DistanceMeasureImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DummySourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class ConflictFQualityCalculator implements FQualityCalculator {
    private final DistanceMeasure distanceMeasure;
    private final SourceQualityCalculator sourceQualityCalculator;
    
    public ConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
        this.sourceQualityCalculator = sourceQualityCalculator; 
    }
    
    public ConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        this(sourceQualityCalculator, new DistanceMeasureImpl());
    }
    
    public ConflictFQualityCalculator() {
        this(new DummySourceQualityCalculator());
    }
    
    protected DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }
    
    protected double getSourceQuality(Resource source, Model metadata) {
        return sourceQualityCalculator.getSourceQuality(source, metadata);
    }
    
    protected abstract double valueQuality(Value value, Collection<Resource> sources, Model metadata);
    
    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
         double valueQuality = valueQuality(value, sources, context.getMetadata());
         
         double resultQuality = valueQuality;

         // Consider conflicting values
         if (context.getResolutionStrategy().getCardinality() == EnumCardinality.SINGLEVALUED 
                 && conflictingStatements.size() > 1) {
             // NOTE: condition conflictingValues.size() > 1 is an optimization that relies on
             // the fact that distance(x,x) = 0 and that value is among conflictingQuads
             
             // Calculated distance average weighted by the respective source qualities
             double distanceAverage = 0;
             double totalSourcesQuality = 0;
             for (Statement statement : conflictingStatements) {
                 double sourceQuality = sourceQualityCalculator.getSourceQuality(statement.getContext(), context.getMetadata());
                 double distance = distanceMeasure.distance(value, getValue(statement));
                 distanceAverage += sourceQuality * distance;
                 totalSourcesQuality += sourceQuality;
             }
             distanceAverage /= totalSourcesQuality;
             resultQuality = resultQuality * (1 - distanceAverage);
         }

         return resultQuality;
    }

    protected Value getValue(Statement statement) {
        return statement.getObject(); 
    }
}
