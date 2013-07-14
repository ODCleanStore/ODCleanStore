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
 * F-quality calculator which takes conflicting values into consideration.
 * A base quality is calculated by {@link #valueQuality()} (abstract, to be overridden by child classes) 
 * and then, if the cardinality in resolution settings is SINGLEVALUE, the quality is decreased with conflicting values
 * according to the following formula:
 * 
 * <code>result_quality = quality * (1 - X)</code>
 * 
 * where <code>X</code> is the average of distances (from {@link DistanceMeasure}) between the evaluated value and 
 * conflicting values weighted by quality of the corresponding sources (from {@link SourceQualityCalculator}). 
 * @author Jan Michelfeit
 */
public abstract class ConflictFQualityCalculator implements FQualityCalculator {
    private final DistanceMeasure distanceMeasure;
    private final SourceQualityCalculator sourceQualityCalculator;
    
    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     * @param distanceMeasure distance measure used to measure the degree of conflict between values
     */
    public ConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
        this.sourceQualityCalculator = sourceQualityCalculator; 
    }
    
    /**
     * Creates a new instance with the default implementation of {@link DistanceMeasure}.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     */
    public ConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        this(sourceQualityCalculator, new DistanceMeasureImpl());
    }

    /**
     * Creates a new instance with the default Implementation of {@link DistanceMeasure} and source quality 
     * score considered to be always {@value DummySourceQualityCalculator#SOURCE_QUALITY}.
     */
    public ConflictFQualityCalculator() {
        this(new DummySourceQualityCalculator());
    }
    
    /**
     * Returns the used distance measure.
     * @return used distance measure
     */
    protected DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }
    
    /**
     * Returns the source quality score for the given source and context metadata.
     * @param source name of the named graph whose quality is being calculated
     * @param metadata context metadata which can be used for calculation
     * @return source quality score of named graph identified by <code>source</code>
     */
    protected double getSourceQuality(Resource source, Model metadata) {
        return sourceQualityCalculator.getSourceQuality(source, metadata);
    }
    
    /**
     * Calculates the base quality of a value before the factor of conflicts is applied.
     * @param value Value to get quality of
     * @param sources URIs of named graphs claiming the given value
     * @param metadata context metadata which can be used for calculation
     * @return base quality of the given value as a number from interval [0,1]
     */
    protected abstract double valueQuality(Value value, Collection<Resource> sources, Model metadata);
    
    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, 
            Collection<Resource> sources, CRContext context) {
        
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

    /**
     * Returns the part of a quad (statement) to be compared to conflicting values - object in this implementation.
     * @param statement an RDF quad
     * @return object of the given <code>statement</code>
     */
    protected Value getValue(Statement statement) {
        return statement.getObject(); 
    }
}
