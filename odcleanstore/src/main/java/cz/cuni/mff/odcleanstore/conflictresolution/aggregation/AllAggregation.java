package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.TripleItemComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Aggregation method that returns all input triples unchanged except for
 * implicit conflict resolution.
 * Identical triples are aggregated to a single triple with sourceNamedGraphs
 * containg the union of all original named graphs. Otherwise leaves triples
 * as they are and adds a quality estimate.
 * 
 * @author Jan Michelfeit
 */
final class AllAggregation extends SelectedValueAggregation {
    /**
     * A comparator used to sort quads by object and named graph.
     */
    private static final ObjectNamedGraphComparator OBJECT_NG_COMPARATOR;

    /**
     * Comparator of {@link Quad Quads} comparing first by objects, second
     * by named graph.
     */
    private static class ObjectNamedGraphComparator implements Comparator<Quad> {
        @Override
        public int compare(Quad o1, Quad o2) {
            int objectComparison = TripleItemComparator.compare(
                    o1.getObject(),
                    o2.getObject());
            if (objectComparison != 0) {
                return objectComparison;
            } else {
                return o1.getNamedGraph().compareTo(o2.getNamedGraph());
            }
        }
    }

    /**
     * Initialize object comparator.
     */
    static {
        OBJECT_NG_COMPARATOR = new ObjectNamedGraphComparator();
    }

    /**
     * Returns conflictingQuads unchanged, only wrapped as CRQuads with added
     * quality estimate.
     * 
     * {@inheritDoc}
     * 
     * The time complexity is quadratic with number of conflicting quads
     * (for each quad calculates its quality in linear time).
     * 
     * @param conflictingQuads sdf slkdf jldsafj ůldsakjfůlkdsa jfůlkdsa jfůlkdsjf ůldsjfůlkds
     *        jfůlkdsj fůlkds jflkdsfj ůldsa jfs ldfj
     * @param metadata d fjsd lkfjlkdsf jlds jfl
     *        ljdsf lsdjf lksjdflkjdsf ldsjf ldskjf
     * @param errorStrategy skdfjlkds jflkdsjflds fjfdlkdsjf lkds jf
     *        dslkfjsd lkfjslkfd jfl
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        Collection<CRQuad> result = createResultCollection();

        // Sort quads by object so that we can detect identical triples
        Quad[] sortedQuads = conflictingQuads.toArray(new Quad[0]);
        Arrays.sort(sortedQuads, OBJECT_NG_COMPARATOR);

        Quad lastQuad = null; // quad from the previous iteration
        TripleItem lastObject = null; // lastQuad's object
        double lastQuadQuality = Double.NaN;
        ArrayList<String> sourceNamedGraphs = null; // sources for lastQuad
        for (Quad quad : sortedQuads) {
            TripleItem object = quad.getObject();
            boolean isNewObject = !object.equals(lastObject);

            if (isNewObject && lastQuad != null) {
                // Add lastQuad to result
                Quad resultQuad = new Quad(lastQuad.getTriple(), uriGenerator.nextURI());
                result.add(new CRQuad(resultQuad, lastQuadQuality, sourceNamedGraphs));
            }

            if (isNewObject) {
                // A new object
                lastQuad = quad;
                lastObject = object;
                // Set initial capacity to 1 supposing usually there won't be more sources
                sourceNamedGraphs = new ArrayList<String>(1);
                sourceNamedGraphs.add(quad.getNamedGraph());
                lastQuadQuality = computeQuality(lastQuad, conflictingQuads, metadata);
            } else {
                // A quad with object identical to that of the previous quad
                assert lastQuad != null && lastObject != null;
                assert sourceNamedGraphs != null && sourceNamedGraphs.size() >= 1;
                String lastNamedGraph = sourceNamedGraphs.get(sourceNamedGraphs.size() - 1);
                // Avoid duplicities in sourceNamedGraphs:
                if (!quad.getNamedGraph().equals(lastNamedGraph)) {
                    sourceNamedGraphs.add(quad.getNamedGraph());
                }
            }
        }

        if (lastQuad != null) {
            // Don't forget to add the last quad to result
            Quad resultQuad = new Quad(lastQuad.getTriple(), uriGenerator.nextURI());
            result.add(new CRQuad(resultQuad, lastQuadQuality, sourceNamedGraphs));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return always true
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        return true;
    }
}
