package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads by the lexical form of their object.
 * The object must be a literal
 */
public class StringLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        return quad.getObject().isLiteral();
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        String value1 = quad1.getObject().getLiteralLexicalForm();
        String value2 = quad2.getObject().getLiteralLexicalForm();
        return value1.compareTo(value2);
    }
}
