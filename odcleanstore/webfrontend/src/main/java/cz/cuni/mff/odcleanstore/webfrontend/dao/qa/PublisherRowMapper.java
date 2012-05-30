package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PublisherRowMapper extends CustomRowMapper<Publisher>
{
	public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Publisher
		(
			rs.getLong("id"), 
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("uri"))
		);
	}
}
