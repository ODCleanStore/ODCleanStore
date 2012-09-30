package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class OIOutputRowMapper extends CustomRowMapper<OIOutput> 
{
	private static final long serialVersionUID = 1L;

	public OIOutput mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		logger.debug("oid: " + rs.getInt("oid"));
		logger.debug("ruleId: " + rs.getInt("ruleId"));
		logger.debug("otid: " + rs.getInt("otid"));
		
		return new OIOutput
		(
			rs.getInt("oid"),
			rs.getInt("ruleId"),
			rs.getInt("otid"),
			getBigDecimal(rs, "minConfidence"),
			getBigDecimal(rs, "maxConfidence"),
			rs.getString("filename"),
			mapFileFormat(rs)
		);
	}
	
	private OIFileFormat mapFileFormat(ResultSet rs) throws SQLException
	{
		// note that the getInt method returns 0 if the value of the 
		// corresponding column equals NULL in the represented row
		if (rs.getInt("ffid") == 0)
			return null;
		
		return new OIFileFormat
		(
			rs.getInt("ffid"),
			rs.getString("fflbl"),
			blobToString(rs.getBlob("ffdescr"))
		);
	}
}
