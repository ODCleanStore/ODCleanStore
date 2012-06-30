package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class OIOutputTypeDao extends DaoForEntityWithSurrogateKey<OIOutputType>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_OUTPUT_TYPES";
	
	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIOutputType> rowMapper;
	
	public OIOutputTypeDao()
	{
		this.rowMapper = new OIOutputTypeRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<OIOutputType> getRowMapper() 
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
