package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.LexicalLengthComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.ReverseAggregationComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns the quad with the shortest lexical form of the object.
 * This aggregation is applicable to quads with a literal as their object.
 * @author Jan Michelfeit
 */
/*package*/final class ShortestAggregation extends BestSelectedAggregation {
    private static final AggregationComparator AGGREGATION_COMPARATOR =
            new ReverseAggregationComparator(new LexicalLengthComparator());

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public ShortestAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Quad> conflictingQuads) {
        return AGGREGATION_COMPARATOR;
    }
}