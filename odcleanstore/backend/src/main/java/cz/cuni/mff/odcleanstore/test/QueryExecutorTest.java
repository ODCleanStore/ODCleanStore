package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.shared.NodeComparator;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Testovaci trida pro prototyp Query Execution.
 *
 * Pro spusteni je potreba naimportovat data do databaze. Jsou na bitbucketu v
 * design/SPARQL/URI search/QE_test_data.sql - obsah staci vykonat treba pres Interactive SQL ve Virtuoso Conductoru.
 * Pripadne muzete upravit udaje ke SPARQL endpointu v konstantach nize.
 *
 * Melo by to vypsat vysledne quads (ve jmennych grafech http://odcs.mff.cuni.cz/results/*) a
 * kvalitu a metadata (ve jmennem grafu http://odcs.mff.cuni.cz/metadata/)
 *
 * @author Jan Michelfeit
 */
public class QueryExecutorTest {
    private static final String QUERY_URI = "http://linkedgeodata.org/triplify/node240109189";
    private static final String CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
    private static final String USER = "dba";
    private static final String PASSWORD = "dba";

    public static void main(String[] args) throws ODCleanStoreException, URISyntaxException {
        AggregationSpec aggregationSpec = new AggregationSpec();
        aggregationSpec.setDefaultAggregation(EnumAggregationType.ALL);

        QueryConstraintSpec queryConstraintSpec = new QueryConstraintSpec();

        QueryExecution queryExecution = new QueryExecution(new SparqlEndpoint(CONNECTION_STRING, USER, PASSWORD));
        final NamedGraphSet result = queryExecution.findURI(QUERY_URI, queryConstraintSpec, aggregationSpec);

        printPrettyResult(result);
        // result.write(System.out, "TRIG", "http://baseURI/");
    }

    private static void printPrettyResult(NamedGraphSet result) {
        // Sort quads by graph, subject, property, object
        ArrayList<Quad> resultQuads = new ArrayList();
        Iterator<Quad> quadIt = result.findQuads(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
        while (quadIt.hasNext()) {
            resultQuads.add(quadIt.next());
        }
        Collections.sort(resultQuads, new Comparator<Quad>() {
            @Override
            public int compare(Quad quad1, Quad quad2) {
                int comparison = NodeComparator.compare(quad1.getGraphName(), quad2.getGraphName());
                if (comparison != 0) {
                    return comparison;
                }
                comparison = NodeComparator.compare(quad1.getSubject(), quad2.getSubject());
                if (comparison != 0) {
                    return comparison;
                }
                comparison = NodeComparator.compare(quad1.getPredicate(), quad2.getPredicate());
                if (comparison != 0) {
                    return comparison;
                }
                return NodeComparator.compare(quad1.getObject(), quad2.getObject());
            }
        });

        // Print result
        printQuads(resultQuads);
    }

    private static void printQuads(Iterator quadIt) {
        while (quadIt.hasNext()) {
            System.out.println(quadIt.next().toString());
        }
    }

    private static void printQuads(Collection<Quad> quads) {
        printQuads(quads.iterator());
    }
}
