package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;

public class PropertySettingsRowMapper implements ParameterizedRowMapper<PropertySettings>
{
	private static Logger logger = Logger.getLogger(PropertySettingsRowMapper.class);
	
	public PropertySettings mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		AggregationType aggregationType = new AggregationType
		(
			rs.getLong("id__1"),
			rs.getString("label"),
			rs.getString("description")
		);
		
		MultivalueType multivalueType = new MultivalueType
		(
			rs.getLong("id__2"),
			rs.getString("label__3"),
			rs.getString("description__4")
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
