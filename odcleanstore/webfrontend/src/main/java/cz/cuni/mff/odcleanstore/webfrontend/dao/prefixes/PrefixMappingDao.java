package cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.PrefixMapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PrefixMappingDao extends Dao<PrefixMapping>
{
	public static final String TABLE_NAME = "DB.DBA.SYS_XML_PERSISTENT_NS_DECL";
	
	private ParameterizedRowMapper<PrefixMapping> rowMapper;
	
	public PrefixMappingDao()
	{
		this.rowMapper = new PrefixMappingRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<PrefixMapping> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public PrefixMapping load(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot load rows from table: " + getTableName() + " by id."
		);
	}
	
	@Override
	public PrefixMapping loadRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot load raw rows from table: " + getTableName() + " by id."
		);
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + " by id."
		);
	}
	
	@Override
	public void delete(PrefixMapping item)
	{
		String query = "DELETE FROM " + TABLE_NAME + " WHERE NS_PREFIX = ?";
		Object[] params = { item.getPrefix() };
		
		jdbcTemplate.update(query, params);
	}
	
	@Override
	public void save(PrefixMapping item)
	{
		String query = "INSERT INTO " + TABLE_NAME + " (NS_PREFIX, NS_URL) VALUES (?, ?)";
		Object[] params = { item.getPrefix(), item.getUrl() };
		
		jdbcTemplate.update(query, params);
	}
}
