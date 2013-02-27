package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads by the lexical length of their object.
 * The object must be a literal.
 */
public class LexicalLengthComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        return quad.getObject().isLiteral();
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        assert quad1 != null && quad2 != null;
        assert quad1.getObject().isLiteral() && quad2.getObject().isLiteral();
        return quad1.getObject().getLiteralLexicalForm().length()
                - quad2.getObject().getLiteralLexicalForm().length();
    }
}