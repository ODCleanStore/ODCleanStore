package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class TemporaryGraphsDao extends Dao
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TEMPORARY_GRAPHS";
	
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	public void delete(String graphName) throws Exception
	{
		logger.debug("Clearing temporary graph " + graphName);
		
		String query = "SPARQL CLEAR GRAPH ??";
		Object[] params = { graphName };
		jdbcUpdate(query, params, EnumDatabaseInstance.DIRTY);
		
		query = "DELETE FROM " + getTableName() + " WHERE graphName = ?";
		params = new Object[] { graphName };
		jdbcUpdate(query, params, EnumDatabaseInstance.DIRTY);
	}
	
	public void save(String graphName) throws Exception {
		logger.debug("Registering temporary graph " + graphName);
		
		String query = "INSERT REPLACING INTO " + getTableName() + " (graphName) VALUES (?)";
		Object[] params = { graphName };
		jdbcUpdate(query, params, EnumDatabaseInstance.DIRTY);
	}
}
