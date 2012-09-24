package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;


public class QARuleRowMapper extends CustomRowMapper<QARule>
{
	private static final long serialVersionUID = 1L;

	public QARule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new QARule
		(
			rs.getInt("id"),
			rs.getInt("groupId"),
			blobToString(rs.getBlob("filter")),
			blobToString(rs.getBlob("description")),
			rs.getDouble("coefficient")
		);
	}
}
