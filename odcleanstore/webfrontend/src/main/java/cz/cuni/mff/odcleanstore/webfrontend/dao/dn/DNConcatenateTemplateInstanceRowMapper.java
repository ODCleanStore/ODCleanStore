package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN concatenate template instance Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNConcatenateTemplateInstanceRowMapper extends CustomRowMapper<DNConcatenateTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public DNConcatenateTemplateInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNConcatenateTemplateInstance
		(
			rs.getInt("id"),
			rs.getInt("rawRuleId"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("propertyName")),
			blobToString(rs.getBlob("delimiter"))
		);
	}
}