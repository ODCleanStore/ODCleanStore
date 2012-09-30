package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.util.Pair;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RolesAssignedToUsersRowMapping implements ParameterizedRowMapper<Pair<Integer, Integer>>
{
	public Pair<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pair<Integer, Integer>(
			rs.getInt("userId"),
			rs.getInt("roleId")
		);
	}
}
