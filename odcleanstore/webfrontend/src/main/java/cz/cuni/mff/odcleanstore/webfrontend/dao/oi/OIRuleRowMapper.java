package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The OI rule Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIRuleRowMapper extends CustomRowMapper<OIRule>
{
	private static final long serialVersionUID = 1L;

	public OIRule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIRule
		(
			rs.getInt("id"),
			rs.getInt("groupId"),
			rs.getString("label"),
			blobToString(rs.getBlob("description")),
			rs.getString("linkType"),
			rs.getString("sourceRestriction"),
			rs.getString("targetRestriction"),
			blobToString(rs.getBlob("linkageRule")),
			getBigDecimal(rs, "filterThreshold"),
			getInteger(rs, "filterLimit")
		);
	}
}
