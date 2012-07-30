package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * 
 * @author Dusan
 *
 */
public class DNRuleComponentRowMapper extends CustomRowMapper<DNRuleComponent>
{
	private static final long serialVersionUID = 1L;

	public DNRuleComponent mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		DNRuleComponentType type = new DNRuleComponentType
		(
			rs.getLong("typeId"), 
			rs.getString("typeLbl"), 
			rs.getString("typeDescr")
		);
		
		return new DNRuleComponent
		(
			rs.getLong("id"),
			rs.getLong("ruleId"),
			type,
			blobToString(rs.getBlob("modification")),
			blobToString(rs.getBlob("description"))
		);
	}
}
