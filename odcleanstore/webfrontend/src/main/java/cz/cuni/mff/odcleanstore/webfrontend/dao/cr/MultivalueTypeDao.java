package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

/**
 * The Multivalue type DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class MultivalueTypeDao extends DaoForEntityWithSurrogateKey<MultivalueType> 
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "CR_MULTIVALUE_TYPES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<MultivalueType> rowMapper;
	
	/**
	 * 
	 */
	public MultivalueTypeDao()
	{
		this.rowMapper = new MultivalueTypeRowMapper();
	}

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<MultivalueType> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	protected void deleteRaw(Integer item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
