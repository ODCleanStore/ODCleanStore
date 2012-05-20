package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class AggregationTypeRowMapper implements ParameterizedRowMapper<AggregationType>
{
	public AggregationType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new AggregationType(
			rs.getLong("id"),
			rs.getString("label"),
			rs.getString("description")
		);
	}
}