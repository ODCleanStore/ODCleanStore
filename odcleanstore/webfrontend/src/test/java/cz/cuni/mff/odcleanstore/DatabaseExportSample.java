package cz.cuni.mff.odcleanstore;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import cz.cuni.mff.odcleanstore.shared.Utils;

public class DatabaseExportSample
{
    public static void main(String[] args) throws Exception
    {
        // database connection
        Class driverClass = Class.forName(Utils.JDBC_DRIVER);
        
        Connection jdbcConnection = DriverManager.getConnection
        (
        	"jdbc:virtuoso://localhost:1113/UID=dba/PWD=dba ", 
        	"dba", 
        	"dba"
        );
        
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        
        partialDataSet.addTable("DB.ODCLEANSTORE.PUBLISHERS", "SELECT * FROM DB.ODCLEANSTORE.PUBLISHERS'");
        
        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("db_export.xml"));
        
        System.out.println("DONE");
    }
}