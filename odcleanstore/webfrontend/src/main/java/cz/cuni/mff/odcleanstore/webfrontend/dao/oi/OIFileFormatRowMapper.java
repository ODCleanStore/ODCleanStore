package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OIFileFormatRowMapper extends CustomRowMapper<OIFileFormat>
{
	private static final long serialVersionUID = 1L;

	public OIFileFormat mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIFileFormat(
			rs.getLong("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
