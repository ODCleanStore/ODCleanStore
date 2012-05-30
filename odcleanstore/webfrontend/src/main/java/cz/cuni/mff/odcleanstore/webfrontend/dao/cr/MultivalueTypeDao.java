package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class MultivalueTypeDao extends Dao<MultivalueType> 
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_MULTIVALUE_TYPES";

	private ParameterizedRowMapper<MultivalueType> rowMapper;
	
	public MultivalueTypeDao()
	{
		this.rowMapper = new MultivalueTypeRowMapper();
	}

	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<MultivalueType> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public List<MultivalueType> loadAll() 
	{
		return loadAllRaw();
	}

	@Override
	public MultivalueType load(Long id) 
	{
		return loadRaw(id);
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
