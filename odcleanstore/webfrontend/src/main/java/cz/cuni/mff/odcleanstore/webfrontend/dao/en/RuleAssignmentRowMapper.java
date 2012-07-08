package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RuleAssignmentRowMapper extends CustomRowMapper<RuleAssignment>
{
	private static final long serialVersionUID = 1L;

	public RuleAssignment mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new RuleAssignment
		(
			rs.getLong("id"),
			rs.getLong("transformerInstanceId"),
			rs.getLong("groupId"),
			rs.getString("groupLabel"),
			blobToString(rs.getBlob("groupDescription"))
		);
	}
}
