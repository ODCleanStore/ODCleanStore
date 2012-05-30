package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;


public class QARuleRowMapper extends CustomRowMapper<QARule>
{
	public QARule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new QARule
		(
			rs.getLong("id"),
			blobToString(rs.getBlob("filter")),
			blobToString(rs.getBlob("description")),
			rs.getDouble("coefficient")
		);
	}
}
