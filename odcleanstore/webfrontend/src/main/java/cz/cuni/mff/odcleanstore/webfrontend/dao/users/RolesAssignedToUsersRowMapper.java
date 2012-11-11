package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.util.Pair;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Roles assigned to users Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RolesAssignedToUsersRowMapper implements ParameterizedRowMapper<Pair<Integer, Integer>>
{
	public Pair<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pair<Integer, Integer>(
			rs.getInt("userId"),
			rs.getInt("roleId")
		);
	}
}
