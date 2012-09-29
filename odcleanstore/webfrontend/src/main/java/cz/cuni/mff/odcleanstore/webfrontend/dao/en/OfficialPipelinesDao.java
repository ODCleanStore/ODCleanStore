package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class OfficialPipelinesDao extends Dao
{
	//private static Logger logger = Logger.getLogger(OfficialPipelinesDao.class);
	
	private static final long serialVersionUID = 1L;

	// note that the tables must be listed in the correct order 
	// to avoid errors when deleting a table which is referenced
	// by a foreign key from another table
	private static final String[] RELATED_TABLES_NAMES = 
	{
		"OI_RULES_ASSIGNMENT",
		"QA_RULES_ASSIGNMENT",
		"DN_RULES_ASSIGNMENT",
		"TRANSFORMER_INSTANCES",
		"TRANSFORMERS",
		"PIPELINES"
	};
	
	public void commitPipelinesRelatedTables() throws Exception
	{
		try
		{
			executeInTransaction(new CodeSnippet()
			{
				@Override
				public void execute() throws Exception
				{
					List<String> tableNames = Arrays.asList(RELATED_TABLES_NAMES);
					truncateTables(tableNames);
					
					Collections.reverse(tableNames);
					fillTables(tableNames);
				}
			});
		}
		catch (Exception ex)
		{
			throw new Exception(
				"Could not commit pipelines-related tables, due to: " + ex.getMessage()
			);
		}	
	}
	
	private void fillTables(List<String> tableNames) throws Exception
	{
		for (String tableName : tableNames)
			fillTable(tableName);
	}
	
	private void fillTable(String tableNameSuffix) throws Exception
	{
		String officialTableName = constructOfficialTableName(tableNameSuffix);
		String backupTableName = constructBackupTableName(tableNameSuffix);
		
		String query = "INSERT INTO " + officialTableName + " SELECT * FROM " + backupTableName;
		jdbcUpdate(query);
	}
	
	private void truncateTables(List<String> tableNames) throws Exception
	{
		for (String tableName : tableNames)
			truncateTable(tableName);
	}
	
	private void truncateTable(String tableNameSuffix) throws Exception
	{
		String officialTableName = constructOfficialTableName(tableNameSuffix);
		
		jdbcUpdate("DELETE FROM " + officialTableName);
		jdbcUpdate("set_identity_column('" + officialTableName + "','id', 1)");
	}
	
	private String constructOfficialTableName(String nameSuffix)
	{
		return Dao.TABLE_NAME_PREFIX + nameSuffix;
	}
	
	private String constructBackupTableName(String nameSuffix)
	{
		return Dao.TABLE_NAME_PREFIX + Dao.BACKUP_TABLE_PREFIX + nameSuffix;
	}
}
