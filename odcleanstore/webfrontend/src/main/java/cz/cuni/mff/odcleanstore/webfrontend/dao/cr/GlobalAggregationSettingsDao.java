package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;

public class GlobalAggregationSettingsDao 
{
	private static final String TABLE_NAME_PREFIX = "DB.ODCLEANSTORE.";
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_SETTINGS";
			
	private DaoLookupFactory lookupFactory;
	private transient JdbcTemplate jdbcTemplate;
	
	private ErrorStrategyDao errorStrategyDao;
	private AggregationTypeDao aggregationTypeDao;
	private MultivalueTypeDao multivalueTypeDao;

	/**
	 * 
	 * @param lookupFactory
	 */
	public void setDaoLookupFactory(DaoLookupFactory lookupFactory)
	{
		this.lookupFactory = lookupFactory;
		
		errorStrategyDao = new ErrorStrategyDao();
		errorStrategyDao.setDaoLookupFactory(lookupFactory);
		
		aggregationTypeDao = new AggregationTypeDao();
		aggregationTypeDao.setDaoLookupFactory(lookupFactory);
		
		multivalueTypeDao = new MultivalueTypeDao();
		multivalueTypeDao.setDaoLookupFactory(lookupFactory);
	}
	
	private JdbcTemplate getJdbcTemplate()
	{
		if (jdbcTemplate == null)
		{
			DataSource dataSource = lookupFactory.getDataSource();
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		return jdbcTemplate;
	}
	
	public GlobalAggregationSettings load()
	{
		ErrorStrategy defaultErrorStrategy = loadDefaultErrorStrategy();
		AggregationType defaultAggregationType = loadDefaultAggregationType();
		MultivalueType defaultMultivalueType = loadDefaultMultivalueType();
		
		return new GlobalAggregationSettings
		(
			defaultErrorStrategy, 
			defaultMultivalueType, 
			defaultAggregationType
		);
	}
	
	private ErrorStrategy loadDefaultErrorStrategy()
	{
		String query = "SELECT defaultErrorStrategyId FROM " + TABLE_NAME;
		
		Long currentDefaultErrorStrategyId = getJdbcTemplate().queryForLong(query);
		
		return errorStrategyDao.load(currentDefaultErrorStrategyId);
	}
	
	private AggregationType loadDefaultAggregationType()
	{
		String query = "SELECT defaultAggregationTypeId FROM " + TABLE_NAME;
		
		Long currentDefaultAggregationTypeId = getJdbcTemplate().queryForLong(query);
		
		return aggregationTypeDao.load(currentDefaultAggregationTypeId);
	}
	
	private MultivalueType loadDefaultMultivalueType()
	{
		String query = "SELECT defaultMultivalueTypeId FROM " + TABLE_NAME;
		
		Long currentDefaultMultivalueTypeId = getJdbcTemplate().queryForLong(query);
		
		return multivalueTypeDao.load(currentDefaultMultivalueTypeId);
	}
	
	public void save(GlobalAggregationSettings settings)
	{
		// TODO: obalit pomoci transaction
		
		String deleteQuery = "DELETE FROM " + TABLE_NAME;
		getJdbcTemplate().update(deleteQuery);
		
		String insertQuery = 
			"INSERT INTO " + TABLE_NAME + " " + 
			"VALUES (?, ?, ?)";
		
		Object[] params =
		{
			settings.getDefaultAggregationType().getId(),
			settings.getDefaultMultivalueType().getId(),
			settings.getDefaultErrorStrategy().getId()
		};
		
		getJdbcTemplate().update(insertQuery, params);
	}
}
