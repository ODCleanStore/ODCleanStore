package cz.cuni.mff.odcleanstore.queryexecution;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.AnonId;

import virtuoso.jdbc3.VirtuosoExtendedString;
import virtuoso.jdbc3.VirtuosoRdfBox;
import virtuoso.jdbc3.VirtuosoResultSet;

import java.sql.SQLException;


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

    protected static Node virtuosoResultToNode(java.sql.ResultSet rs, String column) throws SQLException {
        Object o = ((VirtuosoResultSet)rs).getObject(column);

        if (o == null) {
            return null;
        }
        else if (o instanceof VirtuosoExtendedString) {
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
        /*else if(rs.wasNull()) {
            return null;
        }*/
        else {
            String literal = rs.getString(column);
            return Node.createLiteral(literal);
        }
    }
}
