package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.RDFS;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.ReificationStyle;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    protected  static final Node SAME_AS_NODE = Node.createURI(OWL.sameAs);

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


}
