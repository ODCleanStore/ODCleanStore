package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ErrorStrategyRowMapper extends CustomRowMapper<ErrorStrategy>
{
	public ErrorStrategy mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new ErrorStrategy(
			rs.getLong("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
