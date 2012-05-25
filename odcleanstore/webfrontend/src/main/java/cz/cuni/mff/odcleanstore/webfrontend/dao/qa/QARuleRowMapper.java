package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;


public class QARuleRowMapper implements ParameterizedRowMapper<QARule>
{
	public QARule mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new QARule
		(
			rs.getLong("id"),
			rs.getString("filter"),
			rs.getString("description"),
			rs.getDouble("coefficient")
		);
	}
}
