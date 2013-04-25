/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;

/**
 * @author Jan Michelfeit
 */
public abstract class ConflictConfidenceCalculator implements ConfidenceCalculator {
    private final DistanceMeasure distanceMeasure;
    private final SourceConfidenceCalculator sourceConfidenceCalculator;
    
    public ConflictConfidenceCalculator(DistanceMeasure distanceMeasure, SourceConfidenceCalculator sourceConfidenceCalculator) {
        this.distanceMeasure = distanceMeasure;
        this.sourceConfidenceCalculator = sourceConfidenceCalculator; 
    }
    
    public ConflictConfidenceCalculator(DistanceMeasure distanceMeasure) {
        this(distanceMeasure, new DummySourceConfidenceCalculator());
    }
    
    protected DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }
    
    protected double sourceConfidence(Resource source, Model metadata) {
        return sourceConfidenceCalculator.sourceConfidence(source, metadata);
    }
    
    protected abstract double valueConfidence(Value value, Collection<Resource> sources, Model metadata);
    
    @Override
    public double getConfidence(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources, CRContext context) {
         double valueConfidence = valueConfidence(value, sources, context.getMetadata());
         
         double resultConfidence = valueConfidence;

         // Usually, the quality is positive, skip the check
         // if (resultQuality == 0) {
         // return resultQuality; // BUNO
         // }

         // Consider conflicting values
         if (context.getResolutionStrategy().getCardinality() == EnumCardinality.SINGLEVALUE 
                 && conflictingStatements.size() > 1) {
             // NOTE: condition conflictingValues.size() > 1 is an optimization that relies on
             // the fact that distance(x,x) = 0 and that value is among conflictingQuads
             
             // Calculated distance average weighted by the respective source qualities
             double distanceAverage = 0;
             double totalSourcesConfidence = 0;
             for (Statement statement : conflictingStatements) {
                 double sourceConfidence = sourceConfidenceCalculator.sourceConfidence(statement.getContext(), context.getMetadata());
                 double distance = distanceMeasure.distance(value, getValue(statement));
                 distanceAverage += sourceConfidence * distance;
                 totalSourcesConfidence += sourceConfidence;
             }
             distanceAverage /= totalSourcesConfidence;
             resultConfidence = resultConfidence * (1 - distanceAverage);
         }

         return resultConfidence;
    }

    protected Value getValue(Statement statement) {
        return statement.getObject(); 
    }
}
