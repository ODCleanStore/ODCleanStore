package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
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
		
		return new PropertySettings
		(
			rs.getLong("id"),
			rs.getString("property"),
			rs.getBoolean("multivalue"),
			aggregationType
		);
	}
}
