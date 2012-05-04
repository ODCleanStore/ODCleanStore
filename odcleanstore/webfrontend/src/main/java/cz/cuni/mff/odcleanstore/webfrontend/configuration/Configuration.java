package cz.cuni.mff.odcleanstore.webfrontend.configuration;

public class Configuration 
{
	private String gmailAddress;
	private String gmailPassword;
	
	public Configuration(String gmailAddress, String gmailPassword)
	{
		this.gmailAddress = gmailAddress;
		this.gmailPassword = gmailPassword;
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
