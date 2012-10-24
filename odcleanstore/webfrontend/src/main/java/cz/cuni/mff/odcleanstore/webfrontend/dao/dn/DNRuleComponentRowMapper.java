package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The DN rule component Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRuleComponentRowMapper extends CustomRowMapper<DNRuleComponent>
{
	private static final long serialVersionUID = 1L;

	public DNRuleComponent mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		DNRuleComponentType type = new DNRuleComponentType
		(
			rs.getInt("typeId"), 
			rs.getString("typeLbl"), 
			rs.getString("typeDescr")
		);
		
		return new DNRuleComponent
		(
			rs.getInt("id"),
			rs.getInt("ruleId"),
			type,
			blobToString(rs.getBlob("modification")),
			blobToString(rs.getBlob("description"))
		);
	}
}
