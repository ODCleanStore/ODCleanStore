package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads by the odcs:score or their named graph.
 * @see cz.cuni.mff.odcleanstore.vocabulary.ODCS#score
 * @author Jan Michelfeit
 */
public class GraphQualityComparator implements AggregationComparator {
    private GraphQualityCalculator qualityCalculator;

    /**
     * Creates a new instance.
     * @param qualityCalculator calculator of a total source named graph quality.
     */
    public GraphQualityComparator(GraphQualityCalculator qualityCalculator) {
        this.qualityCalculator = qualityCalculator;
    }

    @Override
    public boolean isAggregable(Quad quad) {
        return true;
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        double quality1 = qualityCalculator.getSourceQuality(metadata.getMetadata(quad1.getGraphName()));
        double quality2 = qualityCalculator.getSourceQuality(metadata.getMetadata(quad2.getGraphName()));
        return Double.compare(quality1, quality2);
    }
}
