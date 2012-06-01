package cz.cuni.mff.odcleanstore.webfrontend.systest.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: implement exception-safe resource management
public class InitalDBScriptsRunner 
{
	private Connection jdbcConnection;
	
	public InitalDBScriptsRunner(Connection connection)
	{
		this.jdbcConnection = connection;
	}
	
	public void runScript(BufferedReader reader) throws IOException, SQLException
	{
		String query = "";
		
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			query += line + "\n";
			
			if (line.contains(";"))
			{
				query = query.replace(";", "");
				Statement statement = jdbcConnection.createStatement();
				
				try {
					statement.execute(query);
				}
				catch (SQLException ex) 
				{
					if (!ex.getMessage().startsWith("SR268: "))
						throw ex;
				}
				
				query = "";
			}
		}
	}
}
