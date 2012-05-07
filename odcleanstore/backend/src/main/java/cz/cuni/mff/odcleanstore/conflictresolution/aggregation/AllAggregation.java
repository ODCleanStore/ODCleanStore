package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Aggregation method that returns all input triples unchanged except for
 * implicit conflict resolution.
 * Identical triples are aggregated to a single triple with sourceNamedGraphs
 * containing the union of all original named graphs. Otherwise leaves triples
 * as they are and adds a quality estimate.
 *
 * @author Jan Michelfeit
 */
/*package*/final class AllAggregation extends SelectedValueAggregation {
    /**
     * Expected number of data sources per quad - used to initialize data sources collections.
     */
    private static final int EXPECTED_DATA_SOURCES = 2;

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public AllAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns all conflicting quads with quads having the same object aggregated, wrapped as CRQuad.
     * If conflictingQuads are empty, returns an empty collection.
     *
     * {@inheritDoc}
     *
     * The time complexity is quadratic with number of conflicting quads
     * (for each quad calculates its quality in linear time).
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {
        Collection<CRQuad> result = createResultCollection();

        // Sort quads by object so that we can detect identical triples
        Quad[] sortedQuads = conflictingQuads.toArray(new Quad[0]);
        Arrays.sort(sortedQuads, OBJECT_NG_COMPARATOR);

        Quad lastQuad = null; // quad from the previous iteration
        Node lastObject = null; // lastQuad's object
        ArrayList<String> sourceNamedGraphs = null; // sources for lastQuad
        for (Quad quad : sortedQuads) {
            Node object = quad.getObject();
            boolean isNewObject = !object.sameValueAs(lastObject); // intentionally sameValueAs()

            if (isNewObject && lastQuad != null) {
                // Add lastQuad to result
                Quad resultQuad = new Quad(
                        Node.createURI(uriGenerator.nextURI()),
                        lastQuad.getTriple());
                double quadQuality = computeQualitySelected(
                        lastQuad,
                        sourceNamedGraphs,
                        conflictingQuads,
                        metadata);
                result.add(new CRQuad(resultQuad, quadQuality, sourceNamedGraphs));
                sourceNamedGraphs = null;
            }

            if (isNewObject) {
                // A new object
                lastQuad = quad;
                lastObject = object;
                sourceNamedGraphs = new ArrayList<String>(EXPECTED_DATA_SOURCES);
                sourceNamedGraphs.add(quad.getGraphName().getURI());
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
            double quadQuality = computeQualitySelected(
                    lastQuad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata);
            result.add(new CRQuad(resultQuad, quadQuality, sourceNamedGraphs));
        }
        return result;
    }
}
