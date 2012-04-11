package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.data.QuadCollection;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;

import de.fuberlin.wiwiss.ng4j.Quad;

import virtuoso.jdbc3.VirtuosoExtendedString;
import virtuoso.jdbc3.VirtuosoRdfBox;
import virtuoso.jdbc3.VirtuosoResultSet;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Test of different approaches how to query Virtuoso and comparison of their speed.
 *
 * See test*() methods for details of each approach.
 *
 * @author Jan Michelfeit
 */
public class NamedGraphSpeedTest {
    private static final String SPARQL_QUERY = "SELECT ?graph ?s ?p ?o" +
    		"\n WHERE {" +
    		"\n   GRAPH ?graph { ?s ?p ?o }" +
    		"\n   FILTER (?graph = <%s>)" +
    		"\n }";

    private static final String CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
    private static final String USER = "dba";
    private static final String PASSWORD = "dba";
    private static final long DEFAULT_LIMIT = 50000;
    private static final String NAMED_GRAPH = "http://odcs.mff.cuni.cz/test/dbpedia-infobox";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String namedGraph = NAMED_GRAPH;
        if (args.length > 0) {
            namedGraph = args[0];
        }

        System.out.println("=====================================================================");
        QuadCollection result = new QuadCollection();

        // JDBC 3
        result  = testJdbc3(namedGraph);
        //printQuads(result);
        System.out.println("Quads in the result: " + result.size());

        result = testVirtGraphSelect(namedGraph);
        //printQuads(result);
        System.out.println("Quads in the result: " + result.size());

        result = testVirtGraphConstruct(namedGraph);
        //printQuads(result);
        System.out.println("Quads in the result: " + result.size());
    }

    private static QuadCollection testJdbc3(String namedGraph) throws Exception {
        String sparqlQuery = "SPARQL " + String.format(SPARQL_QUERY + "\n LIMIT %d", namedGraph, DEFAULT_LIMIT);

        Class.forName("virtuoso.jdbc3.Driver");
        Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);

        long startTime = System.currentTimeMillis();

        Statement stmt = connection.createStatement();
        stmt.execute(sparqlQuery);

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("JDBC3 query took %.3f s\n", totalTime / 1000.0);

        ArrayList<Quad> quads = new ArrayList<Quad>();

        startTime = System.currentTimeMillis();
        boolean more = true;
        while (more) {
            java.sql.ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Quad quad = new Quad(
                        Node.createURI(rs.getString(1)), // graph name is always an URI resource
                        virtuosoResultToNode(rs, 2),
                        Node.createURI(rs.getString(1)), // property is always an URI resource
                        virtuosoResultToNode(rs, 4));
                quads.add(quad);
            }
            more = stmt.getMoreResults();
        }
        totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("JDBC3 processing took %.3f s\n", totalTime / 1000.0);
        return new QuadCollection(quads);
    }

    private static QuadCollection testVirtGraphSelect(String namedGraph) throws Exception {
        String sparqlQuery = String.format(SPARQL_QUERY, namedGraph);
        VirtGraph set = new VirtGraph(CONNECTION_STRING, USER, PASSWORD);

        long startTime = System.currentTimeMillis();

        com.hp.hpl.jena.query.Query query = QueryFactory.create(sparqlQuery);
        query.setLimit(DEFAULT_LIMIT);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, set);
        com.hp.hpl.jena.query.ResultSet queryResults = vqe.execSelect();

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("VirtGraph select query took %.3f s\n", totalTime / 1000.0);

        ArrayList<Quad> quads = new ArrayList<Quad>();

        startTime = System.currentTimeMillis();
        while (queryResults.hasNext()) {
            QuerySolution solution = queryResults.next();
            Quad quad = new Quad(
                    solution.get("graph").asNode(),
                    solution.get("s").asNode(),
                    solution.get("p").asNode(),
                    solution.get("o").asNode());
            quads.add(quad);
        }
        totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("VirtGraph select processing took %.3f s\n", totalTime / 1000.0);
        return new QuadCollection(quads);
    }

    private static QuadCollection testVirtGraphConstruct(String namedGraph) throws Exception {
        String constructQueryFormat = "CONSTRUCT {?s ?p ?o}" +
                "\n WHERE {" +
                "\n   GRAPH ?graph { ?s ?p ?o }" +
                "\n   FILTER (?graph = <%s>)" +
                "\n }" +
                "\n LIMIT %d";
        String sparqlQuery = String.format(constructQueryFormat, namedGraph, DEFAULT_LIMIT);

        VirtGraph set = new VirtGraph(CONNECTION_STRING, USER, PASSWORD);

        long startTime = System.currentTimeMillis();

        com.hp.hpl.jena.query.Query query = QueryFactory.create(sparqlQuery);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, set);
        Model model = vqe.execConstruct();
        Graph g = model.getGraph();

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("VirtGraph construct query took %.3f s\n", totalTime / 1000.0);

        ArrayList<Quad> quads = new ArrayList<Quad>();

        startTime = System.currentTimeMillis();
        for (Iterator<Triple> i = g.find(Node.ANY, Node.ANY, Node.ANY); i.hasNext();) {
            Triple t = i.next();
            Quad quad = new Quad(
                    Node.createURI("http://unknown"),
                    t.getSubject(),
                    t.getPredicate(),
                    t.getObject());
            quads.add(quad);
        }
        totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("VirtGraph construct processing took %.3f s\n", totalTime / 1000.0);
        return new QuadCollection(quads);
    }

    private static Node virtuosoResultToNode(java.sql.ResultSet rs, int column) throws Exception {
        Object o = ((VirtuosoResultSet)rs).getObject(column);

        if (o instanceof VirtuosoExtendedString) {
            VirtuosoExtendedString vs = (VirtuosoExtendedString) o;
            if (vs.iriType == VirtuosoExtendedString.IRI && (vs.strType & 0x1) == 0x1) {
                String uri = vs.str;
                return Node.createURI(uri);
            } else if (vs.iriType == VirtuosoExtendedString.BNODE) {
                AnonId anonId = new AnonId(vs.str);
                return Node.createAnon(anonId);
            } else {
                String literal = vs.str;
                return Node.createLiteral(literal);
            }
        }
        else if (o instanceof VirtuosoRdfBox) {
            VirtuosoRdfBox rb = (VirtuosoRdfBox) o;
            RDFDatatype datatype = rb.getType() == null
                    ? null
                    : TypeMapper.getInstance().getSafeTypeByName(rb.getType());
            LiteralLabel literal = LiteralLabelFactory.create(rb.rb_box, rb.getLang(), datatype);
            return Node.createLiteral(literal);
        }
        else if(rs.wasNull()) {
            throw new Exception("Query result contains NULL");
        }
        else {
            String literal = rs.getString(column);
            return Node.createLiteral(literal);
        }
    }

    private static void printQuads(Collection<Quad> quads) {
        for (Quad quad : quads) {
            System.out.println(quad.toString());
        }
    }

    private static void printCRQuad(CRQuad crQuad) {
        System.out.printf("%s   %s   %s\n",
                crQuad.getQuad().getTriple().getSubject(),
                crQuad.getQuad().getTriple().getPredicate(),
                crQuad.getQuad().getTriple().getObject());
        // System.out.println("  NG: " + crQuad.getQuad().getNamedGraph());
        System.out.printf("  quality: %.3f\n", crQuad.getQuality());
        System.out.print("  sources: ");
        for (String source : crQuad.getSourceNamedGraphURIs()) {
            System.out.printf("<%s>, ", source);
        }
        System.out.println();
    }
}
