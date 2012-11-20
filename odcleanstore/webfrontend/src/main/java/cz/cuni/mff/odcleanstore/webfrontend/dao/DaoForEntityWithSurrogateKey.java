package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.util.CodeSnippet;

/**
 * Base class for DAOs for business objects with unique ids. 
 * Provides utility classes for loading, deleting and saving. 
 * 
 * Child classes can override the following methods to customize behavior:
 * <ul>
 * 	<li>{@link #deleteRaw(Integer)}</li>
 *  <li>{@link #load(Integer)}</li>
 *  <li>{@link #save(EntityWithSurrogateKey)}</li>
 * </ul>
 * 
 * @author Jan Michelfeit
 * @param <T> type of manipulated business object
 */
public abstract class DaoForEntityWithSurrogateKey<T extends EntityWithSurrogateKey> extends DaoTemplate<T> implements DaoSortableDataProvidable<T>
{
	private static final long serialVersionUID = 1L;
	
	protected static final String KEY_COLUMN = "id";

	/**
	 * Deletes a record from {@link #getTableName()} table with the given id.
	 * @param id id
	 * @throws Exception
	 */
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
		
	/**
	 * Loads the business entity by primary key.
	 */
	public T load(Integer id)
	{
		return loadBy(KEY_COLUMN, id);
	}
	
	/**
	 * Deletes the database record represented by the given business object.
	 */
	public final void delete(T item) throws Exception
	{
		deleteRaw(item.getId());
	}
	
	/**
	 * Delete a record by primary key.
	 */
	public final void delete(Integer id) throws Exception
	{
		deleteRaw(id);
	}
	
	/**
	 * Saves a business object to the database.
	 * If not overridden, throws an {@link UnsupportedOperationException}.
	 */
	public void save(T item) throws Exception {
		throw new UnsupportedOperationException("Cannot insert rows into table " + getTableName() + ".");
	}
	
	/**
	 * Saves a business entity to the database and returns its primary key.
	 */
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
	protected final int getLastInsertId() throws Exception
	{
		String query = "SELECT identity_value()";
		return jdbcQueryForInt(query);
	}
}
