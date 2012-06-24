package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public abstract class DaoForEntityWithSurrogateKey<T extends EntityWithSurrogateKey> extends Dao<T>
{
	private static final long serialVersionUID = 1L;

	public void deleteRaw(Long id) throws Exception
	{
		String query = "DELETE FROM " + getTableName() + " WHERE id = ?";
		Object[] params = { id };
		
		getJdbcTemplate().update(query, params);
	}
		
	public T loadRaw(Long id)
	{
		return loadRawBy("id", id);
	}
	
	public T load(Long id)
	{
		return loadRaw(id);
	}
}
