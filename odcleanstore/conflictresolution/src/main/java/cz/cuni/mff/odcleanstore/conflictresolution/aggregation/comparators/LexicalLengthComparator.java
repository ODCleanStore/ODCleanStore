package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

/**
 * Comparator of quads by the lexical length of their object.
 * The object must be a literal.
 */
public class LexicalLengthComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        return quad.getObject() instanceof Literal;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        assert quad1 != null && quad2 != null;
        return quad1.getObject().stringValue().length()
                - quad2.getObject().stringValue().length();
    }
}