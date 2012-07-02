package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class TransformerInstanceRowMapper extends CustomRowMapper<TransformerInstance>
{
	private static final long serialVersionUID = 1L;
	
	public TransformerInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new TransformerInstance
		(
			rs.getLong("id"),
			rs.getLong("transformerId"),
			rs.getLong("pipelineId"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("workDirPath")),
			blobToString(rs.getBlob("configuration")),
			rs.getInt("priority")
		);
	}
}
