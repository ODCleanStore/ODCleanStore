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
 * Concrete implementation of {@link ConflictFQualityCalculator} suitable
 * for deciding conflict resolution functions (i.e. those choosing one or more values from their input).
 * The base quality (see {@link #valueQuality()}) is determined as the maximum of quality scores of
 * all value's sources. The resulting quality is then increased if there is exact agreement on the evaluated
 * value among multiple sources according to the following formula:
 * 
 * <code>result_quality = quality + (1 - quality) * min(1, support_factor)</code>
 * 
 * where <code>support_factor</code> is
 * 
 * <code>(&lt;sum of quality scores of all sources&gt; - &lt;quality score of the best source&gt;) / agree_coefficient</code>.
 * 
 * <code>agree_coefficient</code> is given in the constructor or the default value {@value #DEFAULT_AGREE_COEFICIENT} is used.
 * 
 * @author Jan Michelfeit
 */
public class DecidingConflictFQualityCalculator extends ConflictFQualityCalculator implements DecidingFQualityCalculator {
    /** The default value of the agree coefficient (see {@link DecidingConflictFQualityCalculator class javadoc}). */
    public static final double DEFAULT_AGREE_COEFICIENT = 4;

    /** Value of the agree coefficient (see {@link DecidingConflictFQualityCalculator class javadoc}). */
    protected final double agreeCoeficient;

    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     * @param agreeCoeficient the agree coefficient (see {@link DecidingConflictFQualityCalculator class javadoc})
     * @param distanceMeasure distance measure used to measure the degree of conflict between values
     */
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator,
            double agreeCoeficient, DistanceMeasure distanceMeasure) {
        super(sourceQualityCalculator, distanceMeasure);
        this.agreeCoeficient = agreeCoeficient;
    }

    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     * @param agreeCoeficient the agree coefficient (see {@link DecidingConflictFQualityCalculator class javadoc})
     */
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator, double agreeCoeficient) {
        super(sourceQualityCalculator);
        this.agreeCoeficient = agreeCoeficient;
    }

    /**
     * Creates a new instance.
     * @param sourceQualityCalculator calculator of quality of source named graphs of the evaluated value
     */
    public DecidingConflictFQualityCalculator(SourceQualityCalculator sourceQualityCalculator) {
        super(sourceQualityCalculator);
        this.agreeCoeficient = DEFAULT_AGREE_COEFICIENT;
    }

    /**
     * Creates a new instance.
     */
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
    public double getFQuality(Value value, Collection<Statement> conflictingStatements,
            Collection<Resource> sources, CRContext context) {

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
