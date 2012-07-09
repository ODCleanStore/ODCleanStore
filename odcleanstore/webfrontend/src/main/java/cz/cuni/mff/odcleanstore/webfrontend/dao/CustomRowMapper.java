package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public abstract class CustomRowMapper<T> implements ParameterizedRowMapper<T>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(CustomRowMapper.class);

	/**
	 * 
	 * @param blob
	 * @return
	 * @throws SQLException
	 */
	protected static String blobToString(Blob blob) throws SQLException
	{
		byte[] content;
		
		try {
			logger.warn("blob: " + blob);
			logger.warn("blob.length(): " + blob.length());
			
			content = blob.getBytes(1, (int)blob.length());
		}
		catch (Exception ex) {
			logger.warn("Cannot read blob: " + ex.getMessage());
			return "";
		}
		
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
