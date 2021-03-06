package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN rename template instance Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRenameTemplateInstanceRowMapper extends CustomRowMapper<DNRenameTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public DNRenameTemplateInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRenameTemplateInstance
		(
			rs.getInt("id"),
			rs.getInt("rawRuleId"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("sourcePropertyName")),
			blobToString(rs.getBlob("targetPropertyName"))
		);
	}
}
