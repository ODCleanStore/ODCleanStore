package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The DN rule component type Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRuleComponentTypeRowMapper extends CustomRowMapper<DNRuleComponentType>
{
	private static final long serialVersionUID = 1L;

	public DNRuleComponentType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new DNRuleComponentType(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
