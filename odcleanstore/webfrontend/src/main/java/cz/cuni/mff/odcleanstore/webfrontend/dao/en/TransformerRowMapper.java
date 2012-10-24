package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Transformer Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class TransformerRowMapper extends CustomRowMapper<Transformer>
{
	private static final long serialVersionUID = 1L;

	public Transformer mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Transformer
		(
			rs.getInt("id"),
			blobToString(rs.getBlob("label")),
			blobToString(rs.getBlob("description")),
			blobToString(rs.getBlob("jarPath")),
			blobToString(rs.getBlob("workDirPath")),
			blobToString(rs.getBlob("fullClassName"))
		);
	}
}
