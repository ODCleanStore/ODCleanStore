package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriterFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;

/**
 * Returns a representation of a query result serialized to the RDF/XML format.
 * The RDF/XML doesn't contain all information, such as quality and source of result quads because
 * statements about triples cannot be represented in RDF/XML (without resorting to reification).
 * @author Jan Michelfeit
 */
public class RDFXMLFormatter extends RDFFormatter {
    private static final RDFWriterFactory WRITER_FACTORY = new RDFXMLPrettyWriterFactory();
    
    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     */
    public RDFXMLFormatter(OutputWSConfig outputWSConfig) {
        super(outputWSConfig);
    }

    @Override
    public Representation format(final BasicQueryResult result, final Reference requestReference) {
        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_XML) {
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
     * @param rdfWriter RDF output writer
     * @param queryResult result of a query
     * @param requestReference Representation of the requested URI
     * @return representation of crQuads and metadata as quads in a NamedGraphSet
     * @throws RDFHandlerException writer error
     */
    private void writeBasic(RDFWriter rdfWriter, BasicQueryResult queryResult, Reference requestReference)
            throws RDFHandlerException {
        // Result data
        int totalResults = 0;
        for (ResolvedStatement resolvedStatement : queryResult.getResultQuads()) {
            totalResults++;
            rdfWriter.handleStatement(resolvedStatement.getStatement());
        }

        // Metadata of source named graphs
        // addODCSNamedGraphMetadata(queryResult.getMetadata(), graph, true);

        // Metadata about the query
        URI requestURI = VALUE_FACTORY.createURI(fixSqBrackets(requestReference.toString(true, false)));
        URI metadataGraphURI = VALUE_FACTORY.createURI(
                outputWSConfig.getResultDataURIPrefix().toString() + ODCSInternal.QUERY_METADATA_GRAPH_URI_INFIX);
        writeBasicQueryMetadata(rdfWriter, requestURI, queryResult, metadataGraphURI);
        Literal totalResultsLiteral = VALUE_FACTORY.createLiteral(totalResults);
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(requestURI, ODCS.TOTAL_RESULTS, totalResultsLiteral));
    }

    @Override
    public Representation format(final MetadataQueryResult metadataResult,
            final GraphScoreWithTrace qaResult, final long totalTime, final Reference requestReference) {

        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_XML) {
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
     * @param rdfWriter RDF output writer
     * @param metadataResult result of metadata query about the requested named graph
     * @param qaResult result of quality assessment over the given named graph; can be null
     * @param totalTime execution time of the query
     * @param requestReference Representation of the requested URI
     * @return representation of the result as quads in a NamedGraphSet
     * @throws RDFHandlerException writer error
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
        URI requestURI = VALUE_FACTORY.createURI(fixSqBrackets(requestReference.toString(true, false)));
        writeBasicQueryMetadata(rdfWriter, requestURI, metadataResult, metadataGraphURI);

        // Additional provenance metadata
        for (Statement quad : metadataResult.getProvenanceMetadata()) {
            rdfWriter.handleStatement(quad);
        }
    }
    
    /**
     * Replaces '[' and ']' by their URI-encoded equivalent.
     * Raw square brackets may be sent by Firefox and it throws an exception during serialization.
     * @param uri URI
     * @return fixed URI
     */
    private String fixSqBrackets(String uri) {
        return uri.replace("[", "%5B").replace("]", "%5D");
    }
}
