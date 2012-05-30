package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class PropertySettingsDao extends Dao<PropertySettings>
{
	private static Logger logger = Logger.getLogger(PropertySettingsDao.class);
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_PROPERTIES";
	
	private ParameterizedRowMapper<PropertySettings> rowMapper;
	
	public PropertySettingsDao()
	{
		this.rowMapper = new PropertySettingsRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<PropertySettings> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void delete(PropertySettings item) 
	{
		deleteRaw(item.getId());
	}

	@Override
	public void save(PropertySettings item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (property, multivalueTypeId, aggregationTypeId) " +
			"VALUES (?, ?, ?)";
		
		Object[] arguments =
		{
			item.getProperty(),
			item.getMultivalueType().getId(),
			item.getAggregationType().getId()
		};
		
		jdbcTemplate.update(query, arguments);
	}

	@Override
	public List<PropertySettings> loadAll() 
	{
		String query = 
			"SELECT * " +
			"FROM " + PropertySettingsDao.TABLE_NAME + " as P " +
			"JOIN " + AggregationTypeDao.TABLE_NAME + " as AT " +
			"ON P.aggregationTypeId = AT.id " +
			"JOIN " + MultivalueTypeDao.TABLE_NAME + " as MT " +
			"ON P.multivalueTypeId = MT.id";
		
		return jdbcTemplate.query(query, getRowMapper());
	}

	@Override
	public PropertySettings load(Long id) 
	{
		throw new UnsupportedOperationException(
			"Cannot load single rows from table: " + getTableName() + "."
		);
	}
}
