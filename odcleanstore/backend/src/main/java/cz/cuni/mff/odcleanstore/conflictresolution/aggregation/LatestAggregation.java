package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.InsertedAtComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns the quad with the longest lexical value of the literal in place of the object.
 * This aggregation is applicable only to quads with a literal as their object.
 * @author Jan Michelfeit
 */
/*package*/final class LatestAggregation extends BestSelectedAggregation {
    private static final AggregationComparator AGGREGATION_COMPARATOR = new InsertedAtComparator();

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public LatestAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Quad> conflictingQuads) {
        return AGGREGATION_COMPARATOR;
    }
}
