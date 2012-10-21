package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The OI output type Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIOutputTypeRowMapper extends CustomRowMapper<OIOutputType>
{
	private static final long serialVersionUID = 1L;

	public OIOutputType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIOutputType(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
