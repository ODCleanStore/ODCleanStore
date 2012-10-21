package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The OI file format Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIFileFormatRowMapper extends CustomRowMapper<OIFileFormat>
{
	private static final long serialVersionUID = 1L;

	public OIFileFormat mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIFileFormat(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
