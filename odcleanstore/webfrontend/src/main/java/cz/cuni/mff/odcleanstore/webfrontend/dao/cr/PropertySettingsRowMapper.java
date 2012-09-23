package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class PropertySettingsRowMapper extends CustomRowMapper<PropertySettings>
{
	private static final long serialVersionUID = 1L;
	
	//private static Logger logger = Logger.getLogger(PropertySettingsRowMapper.class);
	
	public PropertySettings mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		AggregationType aggregationType = new AggregationType
		(
			rs.getLong("atid"),
			rs.getString("atlbl"),
			blobToString(rs.getBlob("atdescr"))
		);
		
		MultivalueType multivalueType = new MultivalueType
		(
			rs.getLong("mtid"),
			rs.getString("mtlbl"),
			blobToString(rs.getBlob("mtdescr"))
		);
		
		return new PropertySettings
		(
			rs.getLong("id"),
			rs.getString("property"),
			multivalueType,
			aggregationType
		);
	}
}
