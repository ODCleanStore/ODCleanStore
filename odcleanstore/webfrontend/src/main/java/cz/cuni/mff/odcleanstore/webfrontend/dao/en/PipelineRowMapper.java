package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Pipeline Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class PipelineRowMapper extends CustomRowMapper<Pipeline>
{
	private static final long serialVersionUID = 1L;

	public Pipeline mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Pipeline
		(
			rs.getInt("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description")),
			rs.getBoolean("isDefault"),
			rs.getBoolean("isLocked"),
			rs.getInt("authorId"),
			blobToString(rs.getBlob("username"))
		);
	}
}
