package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * Row mapper for Relation type entity.
 * 
 * @author Tomas Soukup
 */
public class RelationTypeRowMapper extends CustomRowMapper<RelationType>
{
	private static final long serialVersionUID = 1L;

	public RelationType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new RelationType
		(
			rs.getInt("id"),
			rs.getString("uri")
		);
	}
}