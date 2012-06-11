package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MultivalueTypeRowMapper extends CustomRowMapper<MultivalueType>
{
	public MultivalueType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new MultivalueType(
			rs.getLong("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
