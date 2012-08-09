package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class GlobalAggregationSettingsDao extends Dao<GlobalAggregationSettings>
{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_SETTINGS";
	
	private ParameterizedRowMapper<GlobalAggregationSettings> rowMapper;

	public GlobalAggregationSettingsDao()
	{
		this.rowMapper = new GlobalAggregationSettingsRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<GlobalAggregationSettings> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public GlobalAggregationSettings loadFirstRaw()
	{
		String query =
			"select TOP 1 " + 
			"A.id as aid, A.label as alabel, A.description as adescr, " +
			"M.id as mid, M.label as mlabel, M.description as mdescr, " +
			"E.id as esid, E.label as eslabel, E.description as esdescr " +
			"from DB.ODCLEANSTORE.CR_SETTINGS as S " + 
			"join DB.ODCLEANSTORE.CR_AGGREGATION_TYPES as A on S.defaultAggregationTypeId = A.id " +
			"join DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES as M on S.defaultMultivalueTypeId = M.id " +
			"join DB.ODCLEANSTORE.CR_ERROR_STRaTEGIES as E on S.defaultErrorStrategyId = E.id";
		
		return getJdbcTemplate().queryForObject(query, getRowMapper());
	}
	
	public void save(GlobalAggregationSettings settings)
	{
		String query = 
			"UPDATE " + TABLE_NAME + " SET " +
			"defaultAggregationTypeId = ?, " +
			"defaultMultivalueTypeId = ?, " +
			"defaultErrorStrategyId = ?";
	
		Object[] params =
		{
			settings.getDefaultAggregationType().getId(),
			settings.getDefaultMultivalueType().getId(),
			settings.getDefaultErrorStrategy().getId()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
