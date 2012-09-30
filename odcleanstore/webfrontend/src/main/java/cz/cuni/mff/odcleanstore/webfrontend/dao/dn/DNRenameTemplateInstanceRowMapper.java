package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class DNRenameTemplateInstanceRowMapper extends CustomRowMapper<DNRenameTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public DNRenameTemplateInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRenameTemplateInstance
		(
			rs.getInt("id"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("sourcePropertyName")),
			blobToString(rs.getBlob("targetPropertyName"))
		);
	}
}
