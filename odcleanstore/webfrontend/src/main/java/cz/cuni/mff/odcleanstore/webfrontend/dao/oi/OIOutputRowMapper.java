package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class OIOutputRowMapper extends CustomRowMapper<OIOutput> 
{
	private static final long serialVersionUID = 1L;

	public OIOutput mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new OIOutput
		(
			rs.getLong("id"),
			rs.getLong("ruleId"),
			mapOutputType(rs),
			rs.getDouble("minConfidence"),
			rs.getDouble("maxConfidence"),
			rs.getString("filename"),
			mapFileFormat(rs)
		);
	}
	
	private OIOutputType mapOutputType(ResultSet rs) throws SQLException
	{
		return new OIOutputType
		(
			rs.getLong("otid"),
			rs.getString("otlbl"),
			blobToString(rs.getBlob("otdescr"))
		);
	}
	
	private OIFileFormat mapFileFormat(ResultSet rs) throws SQLException
	{
		// note that the getLong method returns 0 if the value of the 
		// corresponding column equals NULL in the represented row
		if (rs.getLong("ffid") == 0)
			return null;
		
		return new OIFileFormat
		(
			rs.getLong("ffid"),
			rs.getString("fflbl"),
			blobToString(rs.getBlob("ffdescr"))
		);
	}
}
