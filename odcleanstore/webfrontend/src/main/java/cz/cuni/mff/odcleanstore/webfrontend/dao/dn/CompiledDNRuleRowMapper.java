package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN rule Row Mapper for compiled DN rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class CompiledDNRuleRowMapper extends CustomRowMapper<CompiledDNRule>
{
	private static final long serialVersionUID = 1L;

	public CompiledDNRule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new CompiledDNRule
		(
			rs.getInt("groupId"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description"))
		);
	}
}
