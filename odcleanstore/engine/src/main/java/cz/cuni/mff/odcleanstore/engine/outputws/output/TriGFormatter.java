package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.trig.TriGWriterFactory;
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
    private static final RDFWriterFactory WRITER_FACTORY = new TriGWriterFactory();

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
                RDFWriter rdfWriter = WRITER_FACTORY.getWriter(writer);
                try {
                    rdfWriter.startRDF();
                    writeBasic(rdfWriter, result, requestReference);
                    rdfWriter.endRDF();
                } catch (RDFHandlerException e) {
                    throw new IOException(e);
                }
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
     * @throws RDFHandlerException writer error
     */
    private void writeBasic(RDFWriter rdfWriter, BasicQueryResult queryResult, Reference requestReference)
            throws RDFHandlerException {
        
        URI metadataGraphURI = VALUE_FACTORY.createURI(
                outputWSConfig.getResultDataURIPrefix().toString() + ODCSInternal.QUERY_METADATA_GRAPH_URI_INFIX);

        URI requestURI = VALUE_FACTORY.createURI(requestReference.toString(true, false));

        // Data and metadata about the result
        int totalResults = 0;
        for (ResolvedStatement resolvedStatement : queryResult.getResultQuads()) {
            totalResults++;
            rdfWriter.handleStatement(resolvedStatement.getStatement());
            rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                    resolvedStatement.getStatement().getContext(),
                    ODCS.QUALITY,
                    VALUE_FACTORY.createLiteral(resolvedStatement.getQuality()),
                    metadataGraphURI));
            for (Resource sourceNamedGraph : resolvedStatement.getSourceGraphNames()) {
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        resolvedStatement.getStatement().getContext(), 
                        ODCS.SOURCE_GRAPH, 
                        sourceNamedGraph,
                        metadataGraphURI));
            }
            rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                    requestURI,
                    ODCS.RESULT,
                    resolvedStatement.getStatement().getContext(),
                    metadataGraphURI));
        }

        // Metadata of source named graphs
        writeODCSNamedGraphMetadata(rdfWriter, queryResult.getMetadata(), true, metadataGraphURI);

        // Metadata about the query
        writeBasicQueryMetadata(rdfWriter, requestURI, queryResult, metadataGraphURI);
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                requestURI,
                ODCS.TOTAL_RESULTS,
                VALUE_FACTORY.createLiteral(totalResults),
                metadataGraphURI));
    }

    @Override
    public Representation format(final MetadataQueryResult metadataResult,
            final GraphScoreWithTrace qaResult, final long totalTime, final Reference requestReference) {

        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {
            @Override
            public void write(Writer writer) throws IOException {
                RDFWriter rdfWriter = WRITER_FACTORY.getWriter(writer);
                try {
                    rdfWriter.startRDF();
                    writeMetadata(rdfWriter, metadataResult, qaResult, totalTime, requestReference);
                    rdfWriter.endRDF();
                } catch (RDFHandlerException e) {
                    throw new IOException(e);
                }
            };
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    /**
     * Returns a formatted representation of a metadata query result.
     * @param rdfWriter output RDF writer
     * @param metadataResult result of metadata query about the requested named graph
     * @param qaResult result of quality assessment over the given named graph; can be null
     * @param totalTime execution time of the query
     * @param requestReference Representation of the requested URI
     * @return representation of the result as quads in a NamedGraphSet
     * @throws RDFHandlerException 
     */
    private void writeMetadata(RDFWriter rdfWriter, MetadataQueryResult metadataResult,
            GraphScoreWithTrace qaResult, long totalTime, Reference requestReference) throws RDFHandlerException {

        URI namedGraphURI = VALUE_FACTORY.createURI(metadataResult.getQuery());
        URI metadataGraphURI = VALUE_FACTORY.createURI(
                outputWSConfig.getResultDataURIPrefix().toString() + ODCSInternal.QUERY_METADATA_GRAPH_URI_INFIX);

        // Quality Assessment results
        writeQualityAssessmentResults(rdfWriter, namedGraphURI, qaResult, metadataGraphURI);

        // Metadata of source named graphs
        writeODCSNamedGraphMetadata(rdfWriter, metadataResult.getMetadata(), false, metadataGraphURI);

        // Metadata about the query
        URI requestURI = VALUE_FACTORY.createURI(requestReference.toString(true, false));
        writeBasicQueryMetadata(rdfWriter, requestURI, metadataResult, metadataGraphURI);

        // Additional provenance metadata
        Resource additionalProvenanceGraphURI = metadataResult.getProvenanceMetadata().isEmpty()
                ? null
                : metadataResult.getProvenanceMetadata().iterator().next().getContext();
        if (additionalProvenanceGraphURI != null) {
            rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                    namedGraphURI,
                    ODCS.PROVENANCE_METADATA_GRAPH,
                    additionalProvenanceGraphURI,
                    metadataGraphURI));

            for (Statement quad : metadataResult.getProvenanceMetadata()) {
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        quad.getSubject(),
                        quad.getPredicate(),
                        quad.getObject(),
                        additionalProvenanceGraphURI));
            }
        }
    }
}
