package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * The Aggregation type DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class AggregationTypeDao extends DaoForEntityWithSurrogateKey<AggregationType>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_AGGREGATION_TYPES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<AggregationType> rowMapper;
	
	/**
	 * 
	 */
	public AggregationTypeDao()
	{
		this.rowMapper = new AggregationTypeRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer item) throws Exception
	{
		throw new UnsupportedOperationException("Cannot insert rows into table " + getTableName() + ".");
	}

	@Override
	protected ParameterizedRowMapper<AggregationType> getRowMapper() 
	{
		return rowMapper;
	}
}
