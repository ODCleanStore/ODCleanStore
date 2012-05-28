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
		return new Publisher
		(
			rs.getLong("id"), 
			rs.getString("label"),
			rs.getString("uri")
		);
	}
}
