package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public abstract class DaoForEntityWithSurrogateKey<T extends EntityWithSurrogateKey> extends DaoTemplate<T>
{
	private static final long serialVersionUID = 1L;
	
	private static final String KEY_COLUMN = "id";

	protected void deleteRaw(Integer id) throws Exception
	{
		// note that there is no need to surround the deleteRaw operation by 
		// a transaction, as every delete is realized using a single
		// SQL DELETE command - deleting related entities is ensured using
		// CASCADING DELETE constraints
		
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		Object[] params = { id };
		
		logger.debug(KEY_COLUMN + ": " + id);
		
		jdbcUpdate(query, params);
	}
		
	public T load(Integer id)
	{
		return loadBy(KEY_COLUMN, id);
	}
	
	public void delete(T item) throws Exception
	{
		delete(item.getId());
	}
	
	public void delete(Integer id) throws Exception
	{
		deleteRaw(id);
	}
	
	public void save(T item) throws Exception {
		throw new UnsupportedOperationException("Cannot insert rows into table " + getTableName() + ".");
	}
	
	public int saveAndGetKey(final T item) throws Exception
	{
		final SimpleKeyHolder keyHolder = new SimpleKeyHolder();
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				save(item);
				int insertId = getLastInsertId();
				keyHolder.setKey(insertId);
			}
		});
		return keyHolder.getKey();
	}
	
	/**
	 * Returns the last assigned identity column value in the clean database instance.
	 * @return last assigned identity column value
	 * @throws Exception
	 */
	protected int getLastInsertId() throws Exception
	{
		String query = "SELECT identity_value()";
		return jdbcQueryForInt(query);
	}
}
