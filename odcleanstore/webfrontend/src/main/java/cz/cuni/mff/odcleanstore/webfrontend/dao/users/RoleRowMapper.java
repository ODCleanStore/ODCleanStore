package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper extends CustomRowMapper<Role>
{
	private static final long serialVersionUID = 1L;

	public Role mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Role
		(
			rs.getInt("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description"))
		);
	}
}
