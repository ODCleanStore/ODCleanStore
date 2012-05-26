package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;

public class GlobalAggregationSettingsDao 
{
	private static final String TABLE_NAME_PREFIX = "DB.ODCLEANSTORE.";
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_SETTINGS";
			
	private JdbcTemplate jdbcTemplate;
	
	private ErrorStrategyDao errorStrategyDao;
	private AggregationTypeDao aggregationTypeDao;
	private MultivalueTypeDao multivalueTypeDao;

	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// TOOD: mozna spise predavat DaoLookupFactory?
		errorStrategyDao = new ErrorStrategyDao();
		errorStrategyDao.setDataSource(dataSource);
		
		aggregationTypeDao = new AggregationTypeDao();
		aggregationTypeDao.setDataSource(dataSource);
		
		multivalueTypeDao = new MultivalueTypeDao();
		multivalueTypeDao.setDataSource(dataSource);
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
		
		Long currentDefaultErrorStrategyId = jdbcTemplate.queryForLong(query);
		
		return errorStrategyDao.load(currentDefaultErrorStrategyId);
	}
	
	private AggregationType loadDefaultAggregationType()
	{
		String query = "SELECT defaultAggregationTypeId FROM " + TABLE_NAME;
		
		Long currentDefaultAggregationTypeId = jdbcTemplate.queryForLong(query);
		
		return aggregationTypeDao.load(currentDefaultAggregationTypeId);
	}
	
	private MultivalueType loadDefaultMultivalueType()
	{
		String query = "SELECT defaultMultivalueTypeId FROM " + TABLE_NAME;
		
		Long currentDefaultMultivalueTypeId = jdbcTemplate.queryForLong(query);
		
		return multivalueTypeDao.load(currentDefaultMultivalueTypeId);
	}
	
	public void save(GlobalAggregationSettings settings)
	{
		// TODO: obalit pomoci transaction
		
		String deleteQuery = "DELETE FROM " + TABLE_NAME;
		jdbcTemplate.update(deleteQuery);
		
		String insertQuery = 
			"INSERT INTO " + TABLE_NAME + " " + 
			"VALUES (?, ?, ?)";
		
		Object[] params =
		{
			settings.getDefaultAggregationType().getId(),
			settings.getDefaultMultivalueType().getId(),
			settings.getDefaultErrorStrategy().getId()
		};
		
		jdbcTemplate.update(insertQuery, params);
	}
}
