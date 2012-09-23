package cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PrefixDao extends Dao<Prefix>
{
	public static final String TABLE_NAME = "DB.DBA.SYS_XML_PERSISTENT_NS_DECL";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<Prefix> rowMapper;
	
	public PrefixDao()
	{
		this.rowMapper = new PrefixRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Prefix> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void delete(Prefix item)
	{
		String query = "DELETE FROM " + TABLE_NAME + " WHERE NS_PREFIX = ?";
		Object[] params = { item.getPrefix() };
		
		// the delete in the clean DB must preceed the delete in the dirty DB in order
		// for the transactional behavior to work correctly
		// (the operation is surrounded by a transaction on the clean JDBC template)
		//
		getCleanJdbcTemplate().update(query, params);
		getDirtyJdbcTemplate().update(query, params);
	}
	
	@Override
	public void save(Prefix item)
	{
		String query = "INSERT INTO " + TABLE_NAME + " (NS_PREFIX, NS_URL) VALUES (?, ?)";
		Object[] params = { item.getPrefix(), item.getUrl() };

		// the save in the clean DB must preceed the save in the dirty DB in order
		// for the transactional behavior to work correctly
		// (the operation is surrounded by a transaction on the clean JDBC template)
		//
		getCleanJdbcTemplate().update(query, params);
		getDirtyJdbcTemplate().update(query, params);
	}
}
