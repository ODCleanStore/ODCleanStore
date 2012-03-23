package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.fuberlin.wiwiss.ng4j.Quad;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jan Michelfeit
 */
public class NamedGraphQueryTest {
    private static final String SPARQL_QUERY = "SELECT ?graph ?s ?p ?o" +
    		"\n WHERE {" +
    		"\n   GRAPH ?graph { ?s ?p ?o }" +
    		"\n   FILTER (?graph = <%s>)" +
    		"\n }";

    private static final String CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
    private static final String USER = "dba";
    private static final String PASSWORD = "dba";
    private static final long DEFAULT_LIMIT = 100;

    private static final EnumAggregationType DEFAULT_AGGREGATION = EnumAggregationType.ALL;
    private static final EnumAggregationErrorStrategy ERROR_STRATEGY = EnumAggregationErrorStrategy.RETURN_ALL;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ODCleanStoreException {
        if (args.length == 0) {
            System.out.println("Pass an URI as a command line argument");
            return;
        }
        String uri = args[0];

        VirtGraph set = new VirtGraph(CONNECTION_STRING, USER, PASSWORD);
        Query sparql = QueryFactory.create(String.format(SPARQL_QUERY, uri));
        sparql.setLimit(DEFAULT_LIMIT);

        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
        ResultSet sparqlResults = vqe.execSelect();
        assert sparqlResults != null;

        QuadCollection quads = new QuadCollection();
        System.out.println("=====================================================================");
        System.out.println("Source quads:");
        while (sparqlResults.hasNext()) {
            QuerySolution result = sparqlResults.nextSolution();
            Quad quad = new Quad(
                    result.get("graph").asNode(),
                    result.get("s").asNode(),
                    result.get("p").asNode(),
                    result.get("o").asNode());
            quads.add(quad);

            System.out.println(quad.getGraphName() + " { " + quad.getSubject() + " " + quad.getPredicate() + " " + quad.getObject() + " . }");
        }

        ConflictResolverSpec CRSpec = new ConflictResolverSpec("http://example.com/ng-generated/");
        CRSpec.setDefaultAggregation(DEFAULT_AGGREGATION);
        CRSpec.setErrorStrategy(ERROR_STRATEGY);

        // Null means read sameAs links from the input data
        CRSpec.setSameAsLinks(null);

        // Named Graph metadat may be given in a NamedGraphMetadataMap or
        // if null, it is read from input data (see cz.cuni.mff.odcleanstore.vocabulary.ODCS)
        CRSpec.setNamedGraphMetadata(null);

        Set<String> preferredURIs = new HashSet<String>();
        preferredURIs.add(uri);
        CRSpec.setPreferredURIs(preferredURIs);

        System.out.println("=====================================================================");
        System.out.println("Resolved quads:");
        ConflictResolver cr = ConflictResolverFactory.createResolver(CRSpec);
        Collection<CRQuad> resolved = cr.resolveConflicts(quads);
        for (CRQuad crQuad : resolved) {
            printCRQuad(crQuad);
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
