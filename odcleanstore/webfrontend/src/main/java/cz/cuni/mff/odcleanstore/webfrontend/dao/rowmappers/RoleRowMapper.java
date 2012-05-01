package cz.cuni.mff.odcleanstore.webfrontend.dao.rowmappers;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements ParameterizedRowMapper<Role>
{
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Role(
			rs.getLong("id"),
			rs.getString("label"),
			rs.getString("description")
		);
	}
}
