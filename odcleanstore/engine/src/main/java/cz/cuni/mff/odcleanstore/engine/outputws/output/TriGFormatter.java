package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;

/**
 * Returns a representation of a query result serialized to the TriG format.
 * (See http://www4.wiwiss.fu-berlin.de/bizer/TriG/ .)
 * @author Jan Michelfeit
 */
public class TriGFormatter extends RDFFormatter {
    /** {@ ODCS#quality} as a {@link Node}. */
    private static final Node QUALITY_PROPERTY = Node.createURI(ODCS.quality);
    /** {@ ODCS#sourceGraph} as a {@link Node}. */
    private static final Node SOURCE_GRAPH_PROPERTY = Node.createURI(ODCS.sourceGraph);
    /** {@ ODCS#result} as a {@link Node}. */
    private static final Node RESULT_PROPERTY = Node.createURI(ODCS.result);
    /** {@ ODCS#provenanceMetadataGraph} as a {@link Node}. */
    private static final Node PROVENANCE_GRAPH_PROPERTY = Node.createURI(ODCS.provenanceMetadataGraph);

    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     */
    public TriGFormatter(OutputWSConfig outputWSConfig) {
        super(outputWSConfig);
    }

    @Override
    public Representation format(final BasicQueryResult result, final Reference requestReference) {
        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {
            @Override
            public void write(Writer writer) throws IOException {
                basicConvertToNGSet(result, requestReference).write(writer, "TRIG", "" /* baseURI */);
            };
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    /**
     * Returns a representation of crQuads and metadata as quads in a NamedGraphSet.
     * @param queryResult result of a query
     * @param requestReference Representation of the requested URI
     * @return representation of crQuads and metadata as quads in a NamedGraphSet
     */
    private NamedGraphSet basicConvertToNGSet(BasicQueryResult queryResult, Reference requestReference) {
        NamedGraphSet result = new NamedGraphSetImpl();
        NamedGraph metadataGraph = new NamedGraphImpl(
                outputWSConfig.getResultDataURIPrefix().toString() + ODCSInternal.queryMetadataGraphUriInfix,
                Factory.createGraphMem(ReificationStyle.Standard));

        Node requestURI = Node.createURI(requestReference.toString(true, false));

        // Data and metadata about the result
        int totalResults = 0;
        for (CRQuad crQuad : queryResult.getResultQuads()) {
            totalResults++;
            result.addQuad(crQuad.getQuad());
            metadataGraph.add(new Triple(
                    crQuad.getQuad().getGraphName(),
                    QUALITY_PROPERTY,
                    Node.createLiteral(LiteralLabelFactory.create(crQuad.getQuality()))));
            for (String sourceNamedGraph : crQuad.getSourceNamedGraphURIs()) {
                metadataGraph.add(new Triple(
                        crQuad.getQuad().getGraphName(), 
                        SOURCE_GRAPH_PROPERTY, 
                        Node.createURI(sourceNamedGraph)));
            }
            metadataGraph.add(new Triple(requestURI, RESULT_PROPERTY, crQuad.getQuad().getGraphName()));
        }

        // Metadata of source named graphs
        addODCSNamedGraphMetadata(queryResult.getMetadata(), metadataGraph, true);

        // Metadata about the query
        addBasicQueryMetadata(requestURI, queryResult, metadataGraph);
        Node totalResultsLiteral = Node.createLiteral(LiteralLabelFactory.create(totalResults));
        metadataGraph.add(new Triple(requestURI, TOTAL_RESULTS_PROPERTY, totalResultsLiteral));

        result.addGraph(metadataGraph);
        return result;
    }

    @Override
    public Representation format(final MetadataQueryResult metadataResult,
            final GraphScoreWithTrace qaResult, final long totalTime, final Reference requestReference) {

        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {
            @Override
            public void write(Writer writer) throws IOException {
                metadataConvertToNGSet(metadataResult, qaResult, totalTime, requestReference)
                    .write(writer, "TRIG", "" /* baseURI */);
            };
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    /**
     * Returns a formatted representation of a metadata query result.
     * @param metadataResult result of metadata query about the requested named graph
     * @param qaResult result of quality assessment over the given named graph; can be null
     * @param totalTime execution time of the query
     * @param requestReference Representation of the requested URI
     * @return representation of the result as quads in a NamedGraphSet
     */
    private NamedGraphSet metadataConvertToNGSet(MetadataQueryResult metadataResult,
            GraphScoreWithTrace qaResult, long totalTime, Reference requestReference) {

        Node namedGraphURI = Node.createURI(metadataResult.getQuery());

        NamedGraphSet result = new NamedGraphSetImpl();
        NamedGraph metadataGraph = new NamedGraphImpl(
                outputWSConfig.getResultDataURIPrefix().toString() + ODCSInternal.queryMetadataGraphUriInfix,
                Factory.createGraphMem(ReificationStyle.Standard));

        // Quality Assessment results
        addQualityAssessmentResults(namedGraphURI, qaResult, metadataGraph);

        // Metadata of source named graphs
        addODCSNamedGraphMetadata(metadataResult.getMetadata(), metadataGraph, false);

        // Metadata about the query
        Node requestURI = Node.createURI(requestReference.toString(true, false));
        addBasicQueryMetadata(requestURI, metadataResult, metadataGraph);

        // Additional provenance metadata
        if (!metadataResult.getProvenanceMetadata().isEmpty()) {
            Node additionalProvenanceGraphURI = metadataResult.getProvenanceMetadata().iterator().next().getGraphName();
            NamedGraph additionalProvenanceGraph = new NamedGraphImpl(
                    additionalProvenanceGraphURI,
                    Factory.createGraphMem(ReificationStyle.Standard));
            metadataGraph.add(new Triple(namedGraphURI, PROVENANCE_GRAPH_PROPERTY, additionalProvenanceGraphURI));

            for (Quad quad : metadataResult.getProvenanceMetadata()) {
                additionalProvenanceGraph.add(quad.getTriple());
            }

            result.addGraph(additionalProvenanceGraph);
        }

        result.addGraph(metadataGraph);
        return result;
    }
}
