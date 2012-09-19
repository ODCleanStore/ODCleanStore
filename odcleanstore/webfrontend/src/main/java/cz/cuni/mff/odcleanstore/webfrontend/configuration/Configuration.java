package cz.cuni.mff.odcleanstore.webfrontend.configuration;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

public class Configuration 
{
	private JDBCConnectionCredentials cleanConnectionCoords;
	private JDBCConnectionCredentials dirtyConnectionCoords;
	private String gmailAddress;
	private String gmailPassword;
	
	public Configuration(
		JDBCConnectionCredentials cleanConnectionCoords,
		JDBCConnectionCredentials dirtyConnectionCoords,
		String gmailAddress, String gmailPassword)
	{
		this.cleanConnectionCoords = cleanConnectionCoords;
		this.dirtyConnectionCoords = dirtyConnectionCoords;
		this.gmailAddress = gmailAddress;
		this.gmailPassword = gmailPassword;
	}

	public JDBCConnectionCredentials getCleanConnectionCoords() 
	{
		return cleanConnectionCoords;
	}
	
	public JDBCConnectionCredentials getDirtyConnectionCoords()
	{
		return dirtyConnectionCoords;
	}


	public String getGmailAddress() 
	{
		return gmailAddress;
	}

	public String getGmailPassword() 
	{
		return gmailPassword;
	}	
}
