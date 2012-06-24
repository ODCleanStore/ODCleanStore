package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class GlobalAggregationSettingsRowMapper extends CustomRowMapper<GlobalAggregationSettings>
{
	private static final long serialVersionUID = 1L;

	public GlobalAggregationSettings mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		ErrorStrategy defaultErrorStrategy = new ErrorStrategy
		(
			rs.getLong("esid"), 
			rs.getString("eslabel"), 
			rs.getString("esdescr")
		);
		
		MultivalueType defaultMultivalueType = new MultivalueType
		(
			rs.getLong("mid"), 
			rs.getString("mlabel"), 
			rs.getString("mdescr")
		);
		
		AggregationType defaultAggregationType = new AggregationType
		(
			rs.getLong("aid"), 
			rs.getString("alabel"), 
			rs.getString("adescr")
		);
		
		return new GlobalAggregationSettings
		(
			defaultErrorStrategy, 
			defaultMultivalueType, 
			defaultAggregationType
		);
	}
}
