package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN rule Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRuleRowMapper extends CustomRowMapper<DNRule>
{
	private static final long serialVersionUID = 1L;

	public DNRule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRule
		(
			rs.getInt("id"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("description"))
		);
	}
}
