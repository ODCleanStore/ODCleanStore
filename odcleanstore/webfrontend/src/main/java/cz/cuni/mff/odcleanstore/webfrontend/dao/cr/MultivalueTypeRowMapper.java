package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Mutlivalue type Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class MultivalueTypeRowMapper extends CustomRowMapper<MultivalueType>
{
	private static final long serialVersionUID = 1L;
	
	public MultivalueType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new MultivalueType(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
