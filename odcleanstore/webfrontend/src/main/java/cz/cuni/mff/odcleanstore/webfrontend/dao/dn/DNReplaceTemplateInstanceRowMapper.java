package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;


public class DNReplaceTemplateInstanceRowMapper extends CustomRowMapper<DNReplaceTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public DNReplaceTemplateInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNReplaceTemplateInstance
		(
			rs.getLong("id"),
			rs.getLong("groupId"),
			blobToString(rs.getBlob("propertyName")),
			blobToString(rs.getBlob("pattern")),
			blobToString(rs.getBlob("replacement"))
		);
	}
}
