package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import java.util.List;

import org.apache.log4j.Logger;

public class PropertySettingsDao extends Dao<PropertySettings>
{
	private static Logger logger = Logger.getLogger(PropertySettingsDao.class);
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_PROPERTIES";
	
	@Override
	public void delete(PropertySettings item) 
	{
		logger.debug("Deleting cr property settings: " + item.getId());
		
		String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
		
		Object[] arguments =
		{
			item.getId()
		};
		
		jdbcTemplate.update(query, arguments);
	}

	@Override
	public void save(PropertySettings item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (property, multivalue, aggregationTypeId)" +
			"VALUES (?, ?, ?)";
		
		Object[] arguments =
		{
			item.getProperty(),
			item.isMultivalue(),
			item.getAggregationType().getId()
		};
		
		jdbcTemplate.update(query, arguments);
	}

	@Override
	public void update(PropertySettings item) 
	{
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public List<PropertySettings> loadAll() 
	{
		String query = 
			"SELECT P.*, AT.* " +
			"FROM " + PropertySettingsDao.TABLE_NAME + " as P " +
			"JOIN " + AggregationTypeDao.TABLE_NAME + " as AT " +
			"ON P.aggregationTypeId = AT.id";
		
		return jdbcTemplate.query
		(
			query, 
			new PropertySettingsRowMapper()
		);
	}

	@Override
	public PropertySettings load(Long id) 
	{
		return null;
	}
}
