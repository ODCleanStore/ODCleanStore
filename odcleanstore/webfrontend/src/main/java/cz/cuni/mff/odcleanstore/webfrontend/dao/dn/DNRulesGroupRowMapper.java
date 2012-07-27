package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class DNRulesGroupRowMapper extends CustomRowMapper<DNRulesGroup>
{
	private static final long serialVersionUID = 1L;

	public DNRulesGroup mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRulesGroup
		(
			rs.getLong("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description"))
		);
	}
}
