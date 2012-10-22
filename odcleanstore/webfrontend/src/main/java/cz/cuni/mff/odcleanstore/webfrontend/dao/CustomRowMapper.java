package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.math.BigDecimal;

/**
 * An abstract parent of all row mappers used throughout the DAO layer.
 * Provides some utility methods.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <T> the BO to be returned
 */
public abstract class CustomRowMapper<T> implements ParameterizedRowMapper<T>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected static Logger logger = Logger.getLogger(CustomRowMapper.class);

	/**
	 * Accepts the value of a blob attribute and returns its value represented
	 * as a String. Handles empty blobs correctly (returns an empty String).
	 * 
	 * @param blob
	 * @return
	 * @throws SQLException
	 */
	protected static String blobToString(Blob blob) throws SQLException
	{
		byte[] content;
		
		try {
			content = blob.getBytes(1, (int)blob.length());
		}
		catch (Exception ex) {
			return "";
		}
		
		if (content == null)
			return "";
		
		return new String(content);
	}
	
	/**
	 * Returns the value of the given column from the given result set
	 * converted to double. Handles null values correctly (returns null).
	 * 
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	protected static Double getDouble(ResultSet rs, String columnName) throws SQLException
	{
		Double result = rs.getDouble(columnName);
		if (rs.wasNull())
			result = null;
		
		return result;
	}
	
	/**
	 * Returns the value of the given column from the given result set
	 * converted to BigDecimal. Handles null values correctly (returns null).
	 * 
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	protected static BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException
	{
		BigDecimal result = rs.getBigDecimal(columnName);
		if (rs.wasNull())
			result = null;
		
		return result;
	}
	
	/**
	 * Returns the value of the given column from the given result set
	 * converted to integer. Handles null values correctly (returns null).
	 * 
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	protected static Integer getInteger(ResultSet rs, String columnName) throws SQLException
	{
		Integer result = rs.getInt(columnName);
		if (rs.wasNull())
			result = null;
		
		return result;
	}
}
