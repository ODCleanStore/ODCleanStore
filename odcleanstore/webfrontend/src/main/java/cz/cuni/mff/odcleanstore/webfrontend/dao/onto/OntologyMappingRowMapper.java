package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The Ontology mapping Row Mapper.
 * 
 * @author Tomáš Soukup
 *
 */
public class OntologyMappingRowMapper extends CustomRowMapper<RelationType>
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
