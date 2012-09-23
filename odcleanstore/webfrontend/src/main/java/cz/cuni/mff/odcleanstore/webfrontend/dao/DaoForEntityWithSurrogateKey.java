package cz.cuni.mff.odcleanstore.webfrontend.dao;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public abstract class DaoForEntityWithSurrogateKey<T extends EntityWithSurrogateKey> extends Dao<T>
{
	protected static Logger logger = Logger.getLogger(DaoForEntityWithSurrogateKey.class);
	
	private static final long serialVersionUID = 1L;

	public void deleteRaw(Long id) throws Exception
	{
		String query = "DELETE FROM " + getTableName() + " WHERE id = ?";
		Object[] params = { id };
		
		logger.debug("id: " + id);
		
		getCleanJdbcTemplate().update(query, params);
	}
		
	public T loadRaw(Long id)
	{
		return loadRawBy("id", id);
	}
	
	public T load(Long id)
	{
		return loadRaw(id);
	}
	
	public void delete(final Long id) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
	
	/**
	 * Returns the last assigned identity column value in the clean database instance.
	 * @return last assigned identity column value
	 * @throws Exception
	 */
	protected long getLastInsertId() throws Exception
	{
		String query = "SELECT identity_value()";
		return getCleanJdbcTemplate().queryForLong(query);
	}
}
