package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;


public class PublisherRowMapper implements ParameterizedRowMapper<Publisher>
{
	public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		Long id = rs.getLong("id");
		String uriValue = rs.getString("uri");
		
		URI uri;
		
		try 
		{
			uri = new URI(uriValue);
		}
		catch (URISyntaxException ex)
		{
			throw new SQLException(
				"Invalid URI: " + uriValue + " for Publisher with id: " + id
			);
		}
		
		return new Publisher(id, uri);
	}
}
