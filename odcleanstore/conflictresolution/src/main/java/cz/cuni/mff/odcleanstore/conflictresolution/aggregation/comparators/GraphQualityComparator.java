package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

/**
 * Comparator of quads by the odcs:score or their named graph.
 * @see cz.cuni.mff.odcleanstore.vocabulary.ODCS#score
 * @author Jan Michelfeit
 */
public class GraphQualityComparator implements AggregationComparator {
    private final GraphQualityCalculator qualityCalculator;

    /**
     * Creates a new instance.
     * @param qualityCalculator calculator of a total source named graph quality.
     */
    public GraphQualityComparator(GraphQualityCalculator qualityCalculator) {
        this.qualityCalculator = qualityCalculator;
    }

    @Override
    public boolean isAggregable(Statement quad) {
        return true;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        double quality1 = qualityCalculator.getSourceQuality(metadata.getMetadata(quad1.getContext()));
        double quality2 = qualityCalculator.getSourceQuality(metadata.getMetadata(quad2.getContext()));
        return Double.compare(quality1, quality2);
    }
}
