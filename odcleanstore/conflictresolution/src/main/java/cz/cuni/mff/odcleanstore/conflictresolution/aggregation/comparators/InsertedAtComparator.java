package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;

/**
 * Comparator of quads by their w3p:insertedAt time.
 * @see cz.cuni.mff.odcleanstore.vocabulary.ODCS#insertedAt
 * @author Jan Michelfeit
 */
public class InsertedAtComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        return true;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        return AggregationUtils.compareByInsertedAt(quad1, quad2, metadata);
    }
}
