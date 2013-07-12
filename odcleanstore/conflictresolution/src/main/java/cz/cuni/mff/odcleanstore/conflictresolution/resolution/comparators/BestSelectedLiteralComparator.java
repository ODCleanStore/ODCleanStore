package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Value;

/**
 * Comparator of {@link Value RDF nodes} used by {@link cz.cuni.mff.odcleanstore.crold.aggregation.BestSelectedAggregation}.
 * Intended for comparison of {@link org.openrdf.model.Literal literals}.
 * @author Jan Michelfeit
 */
public interface BestSelectedLiteralComparator extends BestSelectedComparator<Value> {
}
