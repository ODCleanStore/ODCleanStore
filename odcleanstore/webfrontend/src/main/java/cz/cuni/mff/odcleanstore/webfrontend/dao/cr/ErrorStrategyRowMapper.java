package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class ErrorStrategyRowMapper implements ParameterizedRowMapper<ErrorStrategy>
{
	public ErrorStrategy mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new ErrorStrategy(
			rs.getLong("id"),
			rs.getString("label"),
			rs.getString("description")
		);
	}
}
