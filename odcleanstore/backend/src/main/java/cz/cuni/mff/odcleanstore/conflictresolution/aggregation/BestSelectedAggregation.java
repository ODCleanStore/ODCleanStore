package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Base class for aggregation methods that select a single best quad based on a given comparison of quads.
 * @author Jan Michelfeit
 */
/*package*/abstract class BestSelectedAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public BestSelectedAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Implementation of {@link #aggregate(Collection, NamedGraphMetadataMap)} that returns the best quad
     * selected from input quads wrapped as CRQuad. The best quad is the quad with the highest order in ordering
     * given by comparator returned by {@link #getComparator(Collection)}.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {
        Collection<CRQuad> result = createResultCollection();
        AggregationComparator comparator = getComparator(conflictingQuads);
        Collection<Quad> aggregableQuads = new ArrayList<Quad>(conflictingQuads.size());
        Quad bestQuad = null; // the best quad so far
        for (Quad quad : conflictingQuads) {
            if (!comparator.isAggregable(quad)) {
                handleNonAggregableObject(quad, conflictingQuads, metadata, result, this.getClass());
                continue;
            }

            aggregableQuads.add(quad);
            if (bestQuad == null || comparator.compare(quad, bestQuad, metadata) > 0) {
                bestQuad = quad;
            }
        }

        if (bestQuad == null) {
            // no aggregable quad
            return result;
        }

        Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), bestQuad.getTriple());
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(bestQuad.getObject(), aggregableQuads);
        double quality = computeQualitySelected(bestQuad, sourceNamedGraphs, aggregableQuads, metadata);
        result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        return result;
    }

    /**
     * Returns a comparator of quads that orders the best quad as having the highest order.
     * @param conflictingQuads input quads to be aggregated
     * @return a comparator of quads that orders the best quad as having the highest order
     */
    protected abstract AggregationComparator getComparator(Collection<Quad> conflictingQuads);
}