package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class PropertySettingsDao extends DaoForEntityWithSurrogateKey<PropertySettings>
{	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_PROPERTIES";

	private static final long serialVersionUID = 1L;
	
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
		
		getJdbcTemplate().update(query, arguments);
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
		
		return getJdbcTemplate().query(query, getRowMapper());
	}
	
	@Override
	public PropertySettings load(Long id)
	{
		String query = 
			"SELECT * " +
			"FROM " + PropertySettingsDao.TABLE_NAME + " as P " +
			"JOIN " + AggregationTypeDao.TABLE_NAME + " as AT " +
			"ON P.aggregationTypeId = AT.id " +
			"JOIN " + MultivalueTypeDao.TABLE_NAME + " as MT " +
			"ON P.multivalueTypeId = MT.id " +
			"WHERE P.id = ?";
		
		Object[] params = { id };
			
		return getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
}
