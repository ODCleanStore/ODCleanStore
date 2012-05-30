package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper extends CustomRowMapper<Role>
{
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Role
		(
			rs.getLong("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description"))
		);
	}
}
