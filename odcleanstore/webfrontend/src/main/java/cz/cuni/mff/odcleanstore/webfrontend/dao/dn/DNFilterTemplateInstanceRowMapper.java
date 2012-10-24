package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN filter template instance Row Mapper.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNFilterTemplateInstanceRowMapper extends CustomRowMapper<DNFilterTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public DNFilterTemplateInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNFilterTemplateInstance
		(
			rs.getInt("id"),
			rs.getInt("rawRuleId"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("propertyName")),
			blobToString(rs.getBlob("pattern")),
			rs.getBoolean("keep")
		);
	}
}
