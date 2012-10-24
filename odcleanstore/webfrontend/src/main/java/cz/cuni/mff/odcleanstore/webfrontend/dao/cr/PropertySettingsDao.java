package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

/**
 * The Property settings DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class PropertySettingsDao extends DaoForEntityWithSurrogateKey<PropertySettings>
{	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_PROPERTIES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<PropertySettings> rowMapper;
	
	/**
	 * 
	 */
	public PropertySettingsDao()
	{
		this.rowMapper = new PropertySettingsRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<PropertySettings> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(PropertySettings item) throws Exception
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
		
		logger.debug("property: " + item.getProperty());
		logger.debug("multivalueTypeId: " + item.getMultivalueType().getId());
		logger.debug("aggregationTypeId: " + item.getAggregationType().getId());
		
		jdbcUpdate(query, arguments);
	}

	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void update(PropertySettings item) throws Exception
	{
		String query = 
			"UPDATE " + TABLE_NAME + " SET property = ?, multivalueTypeId = ?, aggregationTypeId = ? " +
			"WHERE id = ?";
		
		Object[] arguments =
		{
			item.getProperty(),
			item.getMultivalueType().getId(),
			item.getAggregationType().getId(),
			item.getId()
		};
		
		logger.debug("property: " + item.getProperty());
		logger.debug("multivalueTypeId: " + item.getMultivalueType().getId());
		logger.debug("aggregationTypeId: " + item.getAggregationType().getId());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, arguments);
	}
	
	@Override
	protected String getSelectAndFromClause()
	{
		String query = 
			"SELECT " +
			"	P.id as id, P.property as property, " +
			"	AT.id as atid, AT.label as atlbl, AT.description as atdescr, " +
			"	MT.id as mtid, MT.label as mtlbl, MT.description as mtdescr " +
			"FROM " + PropertySettingsDao.TABLE_NAME + " as P " +
			"JOIN " + AggregationTypeDao.TABLE_NAME + " as AT " +
			"ON P.aggregationTypeId = AT.id " +
			"JOIN " + MultivalueTypeDao.TABLE_NAME + " as MT " +
			"ON P.multivalueTypeId = MT.id ";
		return query;
	}

	@Override
	public PropertySettings load(Integer id)
	{
		return loadBy("P.id", id);
	}
}
