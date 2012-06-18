package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class OIRuleRowMapper extends CustomRowMapper<OIRule>
{
	private static final long serialVersionUID = 1L;

	public OIRule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIRule
		(
			rs.getLong("id"),
			rs.getLong("groupId"),
			blobToString(rs.getBlob("definition"))
		);
	}
}
