package cz.cuni.mff.odcleanstore.webfrontend.configuration;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

public class Configuration 
{
	private ConnectionCredentials connectionCoords;
	private String gmailAddress;
	private String gmailPassword;
	
	public Configuration(ConnectionCredentials connectionCoords, String gmailAddress, 
		String gmailPassword)
	{
		this.connectionCoords = connectionCoords;
		this.gmailAddress = gmailAddress;
		this.gmailPassword = gmailPassword;
	}

	public ConnectionCredentials getConnectionCoords() 
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
