package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PipelineRowMapper extends CustomRowMapper<Pipeline>
{
	public Pipeline mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pipeline
		(
			rs.getLong("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description")),
			rs.getBoolean("runOnCleanDB")
		);
	}
}
