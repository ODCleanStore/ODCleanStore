package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;
import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Returns a representation of a query result serialized to the TriG format.
 * (See http://www4.wiwiss.fu-berlin.de/bizer/TriG/ .)
 * @author Jan Michelfeit
 */
public class TriGFormatter extends ResultFormatterBase {
    /** {@ ODCS#quality} as a {@link Node}. */
    private static final Node QUALITY_PROPERTY = Node.createURI(ODCS.quality);
    /** {@ ODCS#score} as a {@link Node}. */
    private static final Node SCORE_PROPERTY = Node.createURI(ODCS.score);
    /** {@ ODCS#publisherScore} as a {@link Node}. */
    private static final Node PUBLISHER_SCORE_PROPERTY = Node.createURI(ODCS.publisherScore);
    /** {@ W3P#source} as a {@link Node}. */
    private static final Node SOURCE_PROPERTY = Node.createURI(W3P.source);
    /** {@ W3P#insertedAt} as a {@link Node}. */
    private static final Node INSERTED_AT_PROPERTY = Node.createURI(W3P.insertedAt);
    /** {@ W3P#publishedBy} as a {@link Node}. */
    private static final Node PUBLISHED_BY_PROPERTY = Node.createURI(W3P.publishedBy);
    /** {@ DC#license} as a {@link Node}. */
    private static final Node LICENSE_PROPERTY = Node.createURI(DC.license);
    
	/** URI of named graph where metadata are placed. TODO: load from global configuration */
    public static final String METADATA_GRAPH = "http://odcs.mff.cuni.cz/metadata/";
    
	@Override
	public Representation format(final QueryResult result) {
		WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {
			@Override
			public void write(Writer writer) throws IOException {
				// TODO: baseURI ?
				convertToNGSet(result.getResultQuads(), result.getMetadata()).write(writer, "TRIG", "" /* baseURI */);
			};
		};
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}

    /**
     * Returns a representation of crQuads and metadata as quads in a NamedGraphSet.
     * @param crQuads result quads
     * @param metadata provenance metadata of result quads
     * @return representation of crQuads and metadata as quads in a NamedGraphSet
     */
    private NamedGraphSet convertToNGSet(Collection<CRQuad> crQuads, NamedGraphMetadataMap metadata) {
        NamedGraphSet result = new NamedGraphSetImpl();
        NamedGraph metadataGraph = new NamedGraphImpl(
                METADATA_GRAPH,
                Factory.createGraphMem(ReificationStyle.Standard));

        for (CRQuad crQuad : crQuads) {
            result.addQuad(crQuad.getQuad());
            metadataGraph.add(new Triple(
                    crQuad.getQuad().getGraphName(),
                    QUALITY_PROPERTY,
                    Node.createLiteral(LiteralLabelFactory.create(crQuad.getQuality()))));
            for (String sourceNamedGraph : crQuad.getSourceNamedGraphURIs()) {
                metadataGraph.add(new Triple(
                        crQuad.getQuad().getGraphName(),
                        SOURCE_PROPERTY,
                        Node.createURI(sourceNamedGraph)));
            }
        }

        // Metadata
        for (NamedGraphMetadata graphMetadata : metadata.listMetadata()) {
            Node namedGraphURI = Node.createURI(graphMetadata.getNamedGraphURI());
            String dataSource = graphMetadata.getSource();
            if (dataSource != null) {
                metadataGraph.add(new Triple(namedGraphURI, SOURCE_PROPERTY, Node.createURI(dataSource)));
            }

            Double score = graphMetadata.getScore();
            if (score != null) {
                LiteralLabel literal = LiteralLabelFactory.create(score);
                metadataGraph.add(new Triple(namedGraphURI, SCORE_PROPERTY, Node.createLiteral(literal)));
            }

            Date storedAt = graphMetadata.getInsertedAt();
            if (storedAt != null) {
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(XSD.dateTime.getURI());
                LiteralLabel literal = LiteralLabelFactory.create(storedAt, null, datatype);
                metadataGraph.add(new Triple(namedGraphURI, INSERTED_AT_PROPERTY, Node.createLiteral(literal)));
            }

            String publisher = graphMetadata.getPublisher();
            if (publisher != null) {
                metadataGraph.add(new Triple(namedGraphURI, PUBLISHED_BY_PROPERTY, Node.createURI(publisher)));
            }
            
            String license = graphMetadata.getLicence();
            if (license != null) {
            	metadataGraph.add(new Triple(namedGraphURI, LICENSE_PROPERTY, Node.createURI(license)));
            }

            Double publisherScore = graphMetadata.getPublisherScore();
            if (publisherScore != null) {
                LiteralLabel literal = LiteralLabelFactory.create(publisherScore);
                metadataGraph.add(new Triple(namedGraphURI, PUBLISHER_SCORE_PROPERTY, Node.createLiteral(literal)));
            }
        }
        result.addGraph(metadataGraph);

        return result;
    }
}
