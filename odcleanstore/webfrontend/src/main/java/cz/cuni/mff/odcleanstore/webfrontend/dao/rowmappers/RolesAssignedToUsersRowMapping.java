package cz.cuni.mff.odcleanstore.webfrontend.dao.rowmappers;

import cz.cuni.mff.odcleanstore.util.Pair;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RolesAssignedToUsersRowMapping implements ParameterizedRowMapper<Pair<Long, Long>>
{
	public Pair<Long, Long> mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pair<Long, Long>(
			rs.getLong("userId"),
			rs.getLong("roleId")
		);
	}
}
