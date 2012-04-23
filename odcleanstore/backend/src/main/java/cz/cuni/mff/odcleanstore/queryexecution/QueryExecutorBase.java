package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.RDFS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.vocabulary.XSD;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

/**
 * The base class of query executors - classes that handle each type of query over the clean
 * database.
 *
 * Each query executor loads triples relevant for the query from the clean database, applies
 * conflict resolution to it and converts the result to plain RDF quads.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class QueryExecutorBase {

    // TODO: remove
    protected static final String NG_PREFIX_FILTER = "http://odcs.mff.cuni.cz/namedGraph/qe-test/";
    protected static final String CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
    protected static final String USER = "dba";
    protected static final String PASSWORD = "dba";
    protected static final long DEFAULT_LIMIT = 200;
    protected static final String RESULT_GRAPH_PREFIX = "http://odcs.mff.cuni.cz/results/";
    protected static final String METADATA_GRAPH = "http://odcs.mff.cuni.cz/metadata/";

    protected static final String[] LABEL_PROPERTIES = new String[] { RDFS.label };
    protected static final String LABEL_PROPERTIES_LIST;
    protected  static final Node SAME_AS_PROPERTY = Node.createURI(OWL.sameAs); // TODO
    protected  static final Node QUALITY_PROPERTY = Node.createURI(ODCS.quality);
    protected  static final Node SOURCE_PROPERTY = Node.createURI(W3P.source);

    static {
        assert (LABEL_PROPERTIES.length > 0);
        StringBuilder sb = new StringBuilder();
        for (String property : LABEL_PROPERTIES) {
            sb.append('<');
            sb.append(property);
            sb.append(">, ");
        }
        LABEL_PROPERTIES_LIST = sb.substring(0, sb.length() - 2);
    }

    protected static WrappedResultSet executeQuery(String query) throws ODCleanStoreException {
        try {
            Class.forName("virtuoso.jdbc3.Driver"); // TODO: move
        } catch (ClassNotFoundException e) {
            throw new ODCleanStoreException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD); // TODO: keep
            Statement statement = connection.createStatement();
            statement.execute(query);

            return new WrappedResultSet(statement);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    protected NamedGraph createMetadataGraph() {
        return new NamedGraphImpl(METADATA_GRAPH, Factory.createGraphMem(ReificationStyle.Standard));
    }

    protected NamedGraphSet convertToNGSet(Collection<CRQuad> crQuads, NamedGraphMetadataMap metadata) {
        NamedGraphSet result = new NamedGraphSetImpl();
        NamedGraph metadataGraph = createMetadataGraph();

        // TODO: optimize?
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
            String dataSource = graphMetadata.getDataSource();
            if (dataSource != null) {
                // TODO: avoid creating new Nodes for properties
                metadataGraph.add(
                        new Triple(namedGraphURI, Node.createURI(W3P.source), Node.createURI(dataSource)));
            }

            Double score = graphMetadata.getScore();
            if (score != null) {
                LiteralLabel literal = LiteralLabelFactory.create(score);
                metadataGraph.add(
                        new Triple(namedGraphURI, Node.createURI(ODCS.score), Node.createLiteral(literal)));
            }

            Date storedAt = graphMetadata.getStored();
            if (storedAt != null) {
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(XSD.dateTime.getURI());
                LiteralLabel literal = LiteralLabelFactory.create(storedAt, null, datatype);
                metadataGraph.add(
                        new Triple(namedGraphURI, Node.createURI(W3P.insertedAt), Node.createLiteral(literal)));
            }

            String publisher = graphMetadata.getPublisher(); // TODO: rename publisher to publishedBy
            if (publisher != null) {
                metadataGraph.add(
                        new Triple(namedGraphURI, Node.createURI(W3P.publishedBy), Node.createURI(publisher)));
            }

            Double publisherScore = graphMetadata.getPublisherScore();
            if (publisherScore != null) {
                LiteralLabel literal = LiteralLabelFactory.create(publisherScore);
                metadataGraph.add(
                        new Triple(namedGraphURI, Node.createURI(ODCS.publisherScore), Node.createLiteral(literal)));
            }
        }
        result.addGraph(metadataGraph);

        return result;
    }


}
