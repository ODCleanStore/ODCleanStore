package cz.cuni.mff.odcleanstore.connection;

import com.hp.hpl.jena.graph.Node;

import virtuoso.jena.driver.VirtGraph;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wrapper of {@link java.sql.ResultSet} that extends support for null values, conversion to {@link Node} and Date and
 * large results split to multiple result sets.
 * This wrapper should be used only for results obtained from the Virtuoso JDBC driver.
 * @author Jan Michelfeit
 */
public class WrappedResultSet {
    /** The wrapped SQL statement. */
    private final Statement statement;

    /** The current result set. */
    private java.sql.ResultSet resultSet;

    /** True iff the current result set is prefetched in {@link #resultSet}. */
    private boolean resultSetPrefetched = false;

    /** True iff there are more result sets available. */
    private boolean more = true;

    /**
     * Create a new instance.
     * @param wrappedStatement the statement to wrap
     */
    /*package*/WrappedResultSet(Statement wrappedStatement) {
        this.statement = wrappedStatement;
    }

    /**
     * Moves the cursor forward one row from its current position.
     * The current result set can be then obtained by {@link #getCurrentResultSet()}.
     * @return true if the new current row is valid; false if there are no more rows
     * @throws SQLException exception
     */
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

    /**
     * Retrieves the current result as a ResultSet object.
     * @return the current result as a ResultSet object
     */
    protected java.sql.ResultSet getCurrentResultSet() {
        return resultSetPrefetched ? resultSet : null;
    }

    /**
     * Returns the wrapped SQL statement.
     * @return the wrapped SQL statement.
     */
    public Statement getWrappedStatement() {
        return statement;
    }

    /**
     * Close the wrapped statement.
     * @throws SQLException exception
     */
    public void close() throws SQLException {
        statement.close();
    }

    /**
     * Close the wrapped statement without throwing an exception.
     */
    public void closeQuietly() {
        try {
            statement.close();
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a Node.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Node getNode(String columnLabel) throws SQLException {
        return objectToNode(resultSet.getObject(columnLabel));
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a Node.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Node getNode(int columnIndex) throws SQLException {
        return objectToNode(resultSet.getObject(columnIndex));
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a String.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public String getString(String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a String.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public String getString(int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a String.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Integer getInt(String columnLabel) throws SQLException {
        int value = resultSet.getInt(columnLabel);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a String.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Integer getInt(int columnIndex) throws SQLException {
        int value = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a double.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Double getDouble(String columnLabel) throws SQLException {
        double value = resultSet.getDouble(columnLabel);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a double.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public Double getDouble(int columnIndex) throws SQLException {
        double value = resultSet.getDouble(columnIndex);
        return resultSet.wasNull() ? null : value;
    }
    
    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a BigDecimal.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return resultSet.getBigDecimal(columnLabel);
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a BigDecimal.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException exception
     */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return resultSet.getBigDecimal(columnIndex);
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a java
     * {@link java.util.Date}.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException the object cannot be converted to Date
     */
    public java.util.Date getJavaDate(String columnLabel) throws SQLException {
        return objectToDate(resultSet.getObject(columnLabel));
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a java
     * {@link java.util.Date}.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException the object cannot be converted to Date
     */
    public java.util.Date getJavaDate(int columnIndex) throws SQLException {
        return objectToDate(resultSet.getObject(columnIndex));
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a java
     * {@link java.util.String}.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException the object cannot be converted to Date
     */
    public String getNString(int columnIndex) throws SQLException {
        /* getNString throws AbstractMethodError :( its somehow broken (virt_jdbc3) */
        Blob blob = resultSet.getBlob(columnIndex);
        return resultSet.wasNull() ? null : blobToString(blob);
    }

    /**
     * Retrieves the value of the designated column in the current row of this ResultSet object as a java
     * {@link java.util.String}.
     * @param columnLabel the label for the column (the name of the column or the name specified with the SQL AS clause)
     * @return the column value; if the value is SQL NULL, the value returned is null
     * @throws SQLException the object cannot be converted to Date
     */
    public String getNString(String columnLabel) throws SQLException {
        /* getNString throws AbstractMethodError :( its somehow broken (virt_jdbc3) */
        Blob blob = resultSet.getBlob(columnLabel);
        return resultSet.wasNull() ? null : blobToString(blob);
    }
    
    /**
     * Converts a Blob object representing an NString value to String.
     * @param blob Blob object representing an NString
     * @return blob converted to string or null
     * @throws SQLException database error accessing the blob value
     */
    private String blobToString(Blob blob) throws SQLException {
        if (blob == null) {
            return null;
        }
        byte[] buffer = blob.getBytes(1, (int) blob.length());
        return buffer == null ? "" : new String(buffer);
    }

    /**
     * Converts a value object to Java {@link java.util.Date}.
     * @param o the converted object
     * @return a date or null if the given object is null
     * @throws SQLException the object cannot be converted to Date
     */
    private static java.util.Date objectToDate(Object o) throws SQLException {
        if (o == null) {
            return null;
        } else if (o instanceof java.util.Date) {
            return (java.util.Date) o;
        } else {
            throw new SQLException("Cannot convert value \"%s\" to java.util.Date", o.toString());
        }
    }

    /**
     * Converts a value object to a {@link Node} instance.
     * The current implementation uses Virtuoso Jena provider.
     *
     * @param o the converted object
     * @return a node instance
     * @throws SQLException exception
     */
    private static Node objectToNode(Object o) throws SQLException {
        return VirtGraph.Object2Node(o);

        // The following code is (almost) what VirtGraph.Object2Node(o) does
        // Since currently VirtGraph.Object2Node() is the only dependency on the Virtuoso Jena provider,
        // the following code can replace the dependency completely. However, it appears that using the original
        // implementation is slightly faster.
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
            // NOTE: in original implementation, there is a conversion in VirtGraph.Timestamp2String()
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
