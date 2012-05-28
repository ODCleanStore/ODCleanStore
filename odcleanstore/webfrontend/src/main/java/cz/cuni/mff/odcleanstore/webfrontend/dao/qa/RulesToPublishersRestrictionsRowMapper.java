package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import cz.cuni.mff.odcleanstore.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class RulesToPublishersRestrictionsRowMapper implements ParameterizedRowMapper<Pair<Long, Long>>
{
	public Pair<Long, Long> mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pair<Long, Long>
		(
			rs.getLong("ruleId"),
			rs.getLong("publisherId")
		);
	}
}
