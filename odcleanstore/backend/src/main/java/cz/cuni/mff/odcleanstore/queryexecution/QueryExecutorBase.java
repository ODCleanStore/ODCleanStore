package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import com.hp.hpl.jena.graph.Node;

import virtuoso.jena.driver.VirtGraph;

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

    protected static class WrappedResultSet {
        Statement statement;
        java.sql.ResultSet resultSet;
        boolean resultSetPrefetched = false;
        boolean more = true;

        public WrappedResultSet(Statement wrappedStatement) {
            this.statement = wrappedStatement;
        }

        public boolean next() throws SQLException {
            while (more) {
                if (!resultSetPrefetched) {
                    resultSet = statement.getResultSet();
                    resultSetPrefetched = true;
                }
                if (resultSet.next()) {
                    return true;
                }
                more = statement.getMoreResults();
                resultSetPrefetched = false;
            }
            return false;
        }

        public java.sql.ResultSet getCurrentResultSet() {
            return resultSetPrefetched ? resultSet : null;
        }

        public Statement getWrappedStatement() {
            return statement;
        }

        public void close() throws SQLException {
            statement.close();
        }

        Node getNode(String columnLabel) throws SQLException {
            return objectToNode(resultSet.getObject(columnLabel));
        }

        Node getNode(int columnIndex) throws SQLException {
            return objectToNode(resultSet.getObject(columnIndex));
        }

        public String getString(String columnLabel) throws SQLException {
            return resultSet.getString(columnLabel);
        }

        public String getString(int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        public Double getDouble(String columnLabel) throws SQLException {
            double value = resultSet.getDouble(columnLabel);
            return resultSet.wasNull() ? null : value;
        }

        public Double getDouble(int columnIndex) throws SQLException {
            double value = resultSet.getDouble(columnIndex);
            return resultSet.wasNull() ? null : value;
        }

        public java.util.Date getJavaDate(String columnLabel) throws SQLException {
            return objectToDate(resultSet.getObject(columnLabel));
        }

        public  java.util.Date getJavaDate(int columnIndex) throws SQLException {
            return objectToDate(resultSet.getObject(columnIndex));
        }

        private static java.util.Date objectToDate(Object o) throws SQLException{
            if (o == null) {
                return null;
            }
            else if (o instanceof java.util.Date) {
                return (java.util.Date) o;
            } else {
                throw new SQLException("Cannot convert value \"%s\" to java.util.Date", o.toString());
            }
        }

        private static Node objectToNode(Object o) throws SQLException {
            return VirtGraph.Object2Node(o);

            /*
             Object o = ((VirtuosoResultSet)rs).getObject(column);

            if (o == null || rs.wasNull()) {
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
            // This is not in the recommended usage at http://docs.openlinksw.com/virtuoso/VirtuosoDriverJDBC.html,
            // however, the JDBC driver prefers Java types rather than some XML Schema datatypes.
            // This code was reverse-engineered from VirtGraph.Object2Node() in the Virtuoso Jena Driver.
            else if (o instanceof Integer || o instanceof Short) {
                final String integerType = "http://www.w3.org/2001/XMLSchema#integer";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(integerType);
                LiteralLabel literal = LiteralLabelFactory.create(o, null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof Float) {
                final String floatType = "http://www.w3.org/2001/XMLSchema#float";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(floatType);
                LiteralLabel literal = LiteralLabelFactory.create(o, null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof Double) {
                final String doubleType = "http://www.w3.org/2001/XMLSchema#double";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(doubleType);
                LiteralLabel literal = LiteralLabelFactory.create(o, null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof BigDecimal) {
                final String decimalType = "http://www.w3.org/2001/XMLSchema#decimal";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(decimalType);
                LiteralLabel literal = LiteralLabelFactory.create(o, null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof java.sql.Blob) {
                final String hexBinary = "http://www.w3.org/2001/XMLSchema#hexBinary";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(hexBinary);
                LiteralLabel literal = LiteralLabelFactory.create(o.toString(), null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof java.sql.Date) {
                final String dateType = "http://www.w3.org/2001/XMLSchema#date";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(dateType);
                LiteralLabel literal = LiteralLabelFactory.create(o.toString(), null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof java.sql.Timestamp) {
                final String dateTimeType = "http://www.w3.org/2001/XMLSchema#dateTime";
                // TODO: there is some conversion in VirtGraph.Timestamp2String()
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(dateTimeType);
                LiteralLabel literal = LiteralLabelFactory.create(o.toString(), null, datatype);
                return Node.createLiteral(literal);
            }
            else if (o instanceof java.sql.Time) {
                final String dateTimeType = "http://www.w3.org/2001/XMLSchema#time";
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(dateTimeType);
                LiteralLabel literal = LiteralLabelFactory.create(o.toString(), null, datatype);
                return Node.createLiteral(literal);
            }
            else {
                String literal = rs.getString(column);
                return Node.createLiteral(literal);
            }
            */
        }
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


}
