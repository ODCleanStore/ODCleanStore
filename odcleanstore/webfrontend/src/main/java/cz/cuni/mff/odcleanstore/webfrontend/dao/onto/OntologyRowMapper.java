package cz.cuni.mff.odcleanstore.webfrontend.dao.onto;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The Ontology Row Mapper.
 * 
 * @author Tomáš Soukup
 *
 */
public class OntologyRowMapper extends CustomRowMapper<Ontology>
{
	private static final long serialVersionUID = 1L;

	public Ontology mapRow(ResultSet rs, int rowNum) throws SQLException
	{
		return new Ontology
		(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description")),
			rs.getString("graphName"),
			rs.getInt("authorId"),
			blobToString(rs.getBlob("username")),
			blobToString(rs.getBlob("definition"))
		);
	}

}
