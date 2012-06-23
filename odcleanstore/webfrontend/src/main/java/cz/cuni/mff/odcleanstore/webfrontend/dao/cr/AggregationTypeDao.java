package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class AggregationTypeDao extends DaoForEntityWithSurrogateKey<AggregationType>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_AGGREGATION_TYPES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<AggregationType> rowMapper;
	
	public AggregationTypeDao()
	{
		this.rowMapper = new AggregationTypeRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<AggregationType> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
