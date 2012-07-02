package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public abstract class CustomRowMapper<T> implements ParameterizedRowMapper<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param blob
	 * @return
	 * @throws SQLException
	 */
	protected static String blobToString(Blob blob) throws SQLException
	{
		if (blob == null)
			return "";
		
		byte[] content = blob.getBytes(1, (int)blob.length());
		
		if (content == null)
			return "";
		
		return new String(content);
	}
	
	/**
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
