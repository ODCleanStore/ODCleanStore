package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class MultivalueTypeRowMapper implements ParameterizedRowMapper<MultivalueType>
{
	public MultivalueType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new MultivalueType(
			rs.getLong("id"),
			rs.getString("label"),
			rs.getString("description")
		);
	}
}
