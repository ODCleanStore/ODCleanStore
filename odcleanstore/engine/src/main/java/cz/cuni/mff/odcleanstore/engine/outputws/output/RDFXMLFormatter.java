package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.Quad;

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
    /** Representation of RDF/XML language for serialization in Jena. */
    private static final String SERIALIZER_LANG_RDFXML = "RDF/XML"; 

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
                basicConvertToModel(result, requestReference).write(writer, SERIALIZER_LANG_RDFXML /*, baseURI */);
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
    private Model basicConvertToModel(BasicQueryResult queryResult, Reference requestReference) {
        // Graph graph = Factory.createGraphMem(ReificationStyle.Standard)
        Model resultModel = ModelFactory.createDefaultModel();
        Graph graph = resultModel.getGraph();
        
        // Result data
        int totalResults = 0;
        for (CRQuad crQuad : queryResult.getResultQuads()) {
            totalResults++;
            graph.add(crQuad.getQuad().getTriple());
        }

        // Metadata of source named graphs
        //addODCSNamedGraphMetadata(queryResult.getMetadata(), graph, true);

        // Metadata about the query
        Node requestURI = Node.createURI(fixSqBrackets(requestReference.toString(true, false)));
        addBasicQueryMetadata(requestURI, queryResult, graph);
        Node totalResultsLiteral = Node.createLiteral(LiteralLabelFactory.create(totalResults));
        graph.add(new Triple(requestURI, TOTAL_RESULTS_PROPERTY, totalResultsLiteral));

        return resultModel;
    }

    @Override
    public Representation format(final MetadataQueryResult metadataResult,
            final GraphScoreWithTrace qaResult, final long totalTime, final Reference requestReference) {

        WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_XML) {
            @Override
            public void write(Writer writer) throws IOException {
                metadataConvertToModel(metadataResult, qaResult, totalTime, requestReference)
                        .write(writer, SERIALIZER_LANG_RDFXML /*, baseURI */);
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
    private Model metadataConvertToModel(MetadataQueryResult metadataResult,
            GraphScoreWithTrace qaResult, long totalTime, Reference requestReference) {

        Model resultModel = ModelFactory.createDefaultModel();
        Graph graph = resultModel.getGraph();
        
        Node namedGraphURI = Node.createURI(metadataResult.getQuery());

        // Quality Assessment results
        addQualityAssessmentResults(namedGraphURI, qaResult, graph);

        // Metadata of source named graphs
        addODCSNamedGraphMetadata(metadataResult.getMetadata(), graph, false);

        // Metadata about the query
        Node requestURI = Node.createURI(fixSqBrackets(requestReference.toString(true, false)));
        addBasicQueryMetadata(requestURI, metadataResult, graph);

        // Additional provenance metadata
        for (Quad quad : metadataResult.getProvenanceMetadata()) {
            graph.add(quad.getTriple());
        }

        return resultModel;
    }
    
    /**
     * Replaces '[' and ']' by their URI-encoded equivalent.
     * Raw square brackets may be sent by Firefox and it throws an exception during serialization.
     * @param string URI
     * @return fixed URI
     */
    private String fixSqBrackets(String uri) {
        return uri.replace("[", "%5B").replace("]", "%5D");
    }
}
