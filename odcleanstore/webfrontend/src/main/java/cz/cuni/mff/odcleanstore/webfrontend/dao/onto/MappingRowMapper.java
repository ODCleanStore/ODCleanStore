package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Mapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * Row mapper for ontology mappings.
 * @author Tomas Soukup
 */
public class MappingRowMapper extends CustomRowMapper<Mapping>
{
	private static final long serialVersionUID = 1L;

	public Mapping mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Mapping
		(
			rs.getString("sourceUri"),
			rs.getString("targetUri"),
			rs.getString("relationType")
		);
	}
}
