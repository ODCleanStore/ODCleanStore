package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The OI rules group Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIRulesGroupRowMapper extends CustomRowMapper<OIRulesGroup>
{
	private static final long serialVersionUID = 1L;

	public OIRulesGroup mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIRulesGroup
		(
			rs.getInt("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description")),
			rs.getInt("authorId"),
			rs.getBoolean("isUncommitted"),
			blobToString(rs.getBlob("username"))
		);
	}
}
