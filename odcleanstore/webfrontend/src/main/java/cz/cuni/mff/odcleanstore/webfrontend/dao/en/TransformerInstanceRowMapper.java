package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The Transformer instance Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class TransformerInstanceRowMapper extends CustomRowMapper<TransformerInstance>
{
	private static final long serialVersionUID = 1L;
	
	public TransformerInstance mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new TransformerInstance
		(
			rs.getInt("id"),
			rs.getInt("transformerId"),
			rs.getInt("pipelineId"),
			rs.getString("label"),
			blobToString(rs.getBlob("configuration")),
			rs.getBoolean("runOnCleanDB"),
			rs.getInt("priority")
		);
	}
}
