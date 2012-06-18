package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public abstract class CustomRowMapper<T> implements ParameterizedRowMapper<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	protected static String blobToString(Blob blob) throws SQLException
	{
		if (blob == null)
			return "";
		
		byte[] content = blob.getBytes(1, (int)blob.length());
		
		if (content == null)
			return "";
		
		return new String(content);
	}
}
