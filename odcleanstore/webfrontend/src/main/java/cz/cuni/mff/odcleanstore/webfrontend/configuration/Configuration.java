package cz.cuni.mff.odcleanstore.webfrontend.configuration;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

/**
 * Application configuration class.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class Configuration 
{
	/** connection credentials to the clean Virtuoso DB */
	private JDBCConnectionCredentials cleanConnectionCoords;
	
	/** connection credentials to the dirty Virtuoso DB */
	private JDBCConnectionCredentials dirtyConnectionCoords;
	
	/** the address of the GMAIL SMTP account */
	private String gmailAddress;
	
	/** the password for the GMAIL SMTP account */
	private String gmailPassword;

	/**
	 * 
	 * @param cleanConnectionCoords
	 * @param dirtyConnectionCoords
	 * @param gmailAddress
	 * @param gmailPassword
	 */
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

	/**
	 * 
	 * @return
	 */
	public JDBCConnectionCredentials getCleanConnectionCoords() 
	{
		return cleanConnectionCoords;
	}
	
	/**
	 * 
	 * @return
	 */
	public JDBCConnectionCredentials getDirtyConnectionCoords()
	{
		return dirtyConnectionCoords;
	}

	/**
	 * 
	 * @return
	 */
	public String getGmailAddress() 
	{
		return gmailAddress;
	}

	/**
	 * 
	 * @return
	 */
	public String getGmailPassword() 
	{
		return gmailPassword;
	}	
}
