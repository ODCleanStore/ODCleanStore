package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class QARulesGroupRowMapper extends CustomRowMapper<QARulesGroup>
{
	private static final long serialVersionUID = 1L;

	public QARulesGroup mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new QARulesGroup
		(
			rs.getInt("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description")),
			rs.getInt("authorId")
		);
	}
}
