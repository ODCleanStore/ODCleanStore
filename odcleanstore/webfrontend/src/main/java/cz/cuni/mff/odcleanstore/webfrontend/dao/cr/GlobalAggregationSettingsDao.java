package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

/**
 * The Global aggregatin settings DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class GlobalAggregationSettingsDao extends Dao
{
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_SETTINGS";
	
	private ParameterizedRowMapper<GlobalAggregationSettings> rowMapper;

	/**
	 * 
	 */
	public GlobalAggregationSettingsDao()
	{
		this.rowMapper = new GlobalAggregationSettingsRowMapper();
	}
	
	/**
	 * 
	 * @return
	 */
	protected ParameterizedRowMapper<GlobalAggregationSettings> getRowMapper() 
	{
		return rowMapper;
	}

	/**
	 * 
	 * @return
	 */
	public GlobalAggregationSettings loadFirst()
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
		
		return jdbcQueryForObject(query, getRowMapper());
	}
	
	/**
	 * 
	 * @param settings
	 * @throws Exception
	 */
	public void save(GlobalAggregationSettings settings) throws Exception
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
		
		jdbcUpdate(query, params);
	}
}
