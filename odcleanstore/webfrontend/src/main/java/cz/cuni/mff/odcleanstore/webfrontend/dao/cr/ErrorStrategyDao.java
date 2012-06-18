package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class ErrorStrategyDao extends Dao<ErrorStrategy>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_ERROR_STRATEGIES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<ErrorStrategy> rowMapper;
	
	public ErrorStrategyDao()
	{
		this.rowMapper = new ErrorStrategyRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<ErrorStrategy> getRowMapper() 
	{
		return this.rowMapper;
	}
	
	@Override
	public List<ErrorStrategy> loadAll() 
	{
		return loadAllRaw();
	}

	@Override
	public ErrorStrategy load(Long id) 
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
