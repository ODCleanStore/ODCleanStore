package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DNRuleComponentTypeRowMapper extends CustomRowMapper<DNRuleComponentType>
{
	private static final long serialVersionUID = 1L;

	public DNRuleComponentType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRuleComponentType(
			rs.getLong("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
