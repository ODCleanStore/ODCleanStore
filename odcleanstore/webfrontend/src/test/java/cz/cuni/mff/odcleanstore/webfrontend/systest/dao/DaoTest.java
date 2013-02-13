package cz.cuni.mff.odcleanstore.webfrontend.systest.dao;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

public abstract class DaoTest
{
	private static final String TEST_SPRING_CONFIG_LOCATION = "src/test/resources/dao/config/spring.xml";
	
	private InitalDBScriptsRunner scriptRunner;
	private Connection jdbcConnection;
	private IDatabaseConnection dbunitConnection;
	
	protected ApplicationContext ctx;
	
	public DaoTest() throws Exception
	{
		ctx = new FileSystemXmlApplicationContext(TEST_SPRING_CONFIG_LOCATION);

		Class.forName(ODCSUtils.JDBC_DRIVER);
		
		jdbcConnection = DriverManager.getConnection
		(
			"jdbc:virtuoso://localhost:1113/UID=dba/PWD=dba",
			"dba",
			"dba"
		);
		
		dbunitConnection = new DatabaseConnection(jdbcConnection);
		
		scriptRunner = new InitalDBScriptsRunner(jdbcConnection);
	}
	
	protected abstract String getInitialImportScriptLocation();
	
	protected void setUp() throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(getInitialImportScriptLocation()));
		scriptRunner.runScript(in);
	}
	
	/*
	 	=======================================================================
	 	CUSTOM ASSERTION HELPERS
	 	=======================================================================
	 */
	
	protected void assertTableContentEquals(String tableName, File file) 
		throws Exception
	{
		Assertion.assertEquals
		(
			loadExpectedTableContent(tableName, file), 
			loadCurrentTableContent(tableName)
		);
	}
	
	private ITable loadCurrentTableContent(String tableName) throws Exception
	{
		IDataSet databaseDataSet = dbunitConnection.createDataSet();
        return databaseDataSet.getTable(tableName);
	}
	
	private ITable loadExpectedTableContent(String tableName, File file) 
		throws Exception
	{
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(file);
        return expectedDataSet.getTable(tableName);
	}
}
