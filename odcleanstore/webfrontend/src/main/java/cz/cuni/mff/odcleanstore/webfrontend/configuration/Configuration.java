package cz.cuni.mff.odcleanstore.webfrontend.configuration;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

public class Configuration 
{
	private JDBCConnectionCredentials connectionCoords;
	private String gmailAddress;
	private String gmailPassword;
	
	public Configuration(JDBCConnectionCredentials connectionCoords, String gmailAddress, 
		String gmailPassword)
	{
		this.connectionCoords = connectionCoords;
		this.gmailAddress = gmailAddress;
		this.gmailPassword = gmailPassword;
	}

	public JDBCConnectionCredentials getConnectionCoords() 
	{
		return connectionCoords;
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
