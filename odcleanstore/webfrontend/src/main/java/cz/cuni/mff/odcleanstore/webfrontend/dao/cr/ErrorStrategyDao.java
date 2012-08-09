package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class ErrorStrategyDao extends DaoForEntityWithSurrogateKey<ErrorStrategy>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_ERROR_STRATEGIES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<ErrorStrategy> rowMapper;
	
	public ErrorStrategyDao()
	{
		this.rowMapper = new ErrorStrategyRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<ErrorStrategy> getRowMapper() 
	{
		return this.rowMapper;
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
