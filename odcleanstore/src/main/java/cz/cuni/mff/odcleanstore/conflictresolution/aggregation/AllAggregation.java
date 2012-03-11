package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.NodeComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Aggregation method that returns all input triples unchanged except for
 * implicit conflict resolution.
 * Identical triples are aggregated to a single triple with sourceNamedGraphs
 * containing the union of all original named graphs. Otherwise leaves triples
 * as they are and adds a quality estimate.
 *
 * @author Jan Michelfeit
 */
final class AllAggregation extends SelectedValueAggregation {
    /**
     * Expected number of data sources per quad - used to initialize data sources collections.
     */
    private static final int EXPECTED_DATA_SOURCES = 2;

    /**
     * A comparator used to sort quads by object and named graph.
     */
    private static final ObjectNamedGraphComparator OBJECT_NG_COMPARATOR =
            new ObjectNamedGraphComparator();

    /**
     * Comparator of {@link Quad Quads} comparing first by objects, second by named graph.
     */
    private static class ObjectNamedGraphComparator implements Comparator<Quad> {
        @Override
        public int compare(Quad q1, Quad q2) {
            int objectComparison = NodeComparator.compare(q1.getObject(), q2.getObject());
            if (objectComparison != 0) {
                return objectComparison;
            } else {
                return NodeComparator.compare(q1.getGraphName(), q2.getGraphName());
            }
        }
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
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @param errorStrategy {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            EnumAggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        Collection<CRQuad> result = createResultCollection();

        // Sort quads by object so that we can detect identical triples
        Quad[] sortedQuads = conflictingQuads.toArray(new Quad[0]);
        Arrays.sort(sortedQuads, OBJECT_NG_COMPARATOR);

        Quad lastQuad = null; // quad from the previous iteration
        Node lastObject = null; // lastQuad's object
        double lastQuadQuality = Double.NaN;
        ArrayList<String> sourceNamedGraphs = null; // sources for lastQuad
        for (Quad quad : sortedQuads) {
            Node object = quad.getObject();
            boolean isNewObject = !object.sameValueAs(lastObject); // intentionally sameValueAs()

            if (isNewObject && lastQuad != null) {
                // Add lastQuad to result
                Quad resultQuad = new Quad(
                        Node.createURI(uriGenerator.nextURI()),
                        lastQuad.getTriple());
                result.add(new CRQuad(resultQuad, lastQuadQuality, sourceNamedGraphs));
                sourceNamedGraphs = null;
            }

            if (isNewObject) {
                // A new object
                lastQuad = quad;
                lastObject = object;
                sourceNamedGraphs = new ArrayList<String>(EXPECTED_DATA_SOURCES);
                sourceNamedGraphs.add(quad.getGraphName().getURI());
                lastQuadQuality = computeQuality(lastQuad, conflictingQuads, metadata);
            } else {
                // A quad with object identical to that of the previous quad
                assert lastQuad != null && lastObject != null;
                assert sourceNamedGraphs != null && sourceNamedGraphs.size() >= 1;
                String lastNamedGraph = sourceNamedGraphs.get(sourceNamedGraphs.size() - 1);
                // Avoid duplicities in sourceNamedGraphs:
                if (!quad.getGraphName().equals(lastNamedGraph)) {
                    sourceNamedGraphs.add(quad.getGraphName().getURI());
                }
            }
        }

        if (lastQuad != null) {
            // Don't forget to add the last quad to result
            Quad resultQuad = new Quad(
                    Node.createURI(uriGenerator.nextURI()),
                    lastQuad.getTriple());
            result.add(new CRQuad(resultQuad, lastQuadQuality, sourceNamedGraphs));
        }
        return result;
    }
}
