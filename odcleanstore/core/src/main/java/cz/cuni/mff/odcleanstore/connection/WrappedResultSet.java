package cz.cuni.mff.odcleanstore.connection;

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
}
