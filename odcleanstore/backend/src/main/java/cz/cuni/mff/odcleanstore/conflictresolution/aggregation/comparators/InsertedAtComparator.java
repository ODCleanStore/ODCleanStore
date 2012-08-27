package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads by their w3p:insertedAt time.
 * @see cz.cuni.mff.odcleanstore.vocabulary.ODCS#insertedAt
 * @author Jan Michelfeit
 */
public class InsertedAtComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        return true;
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        return AggregationUtils.compareByInsertedAt(quad1, quad2, metadata);
    }
}
