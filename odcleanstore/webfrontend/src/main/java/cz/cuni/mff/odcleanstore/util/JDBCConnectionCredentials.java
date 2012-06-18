package cz.cuni.mff.odcleanstore.util;

import java.io.Serializable;

/**
 * Encapsulates the coordinates of a JDBC connection.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class JDBCConnectionCredentials implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String driverClassName;
    private String connectionString;
    private String username;
    private String password;

    /**
     * Creates a new instance.
     * @param connectionString JDBC connection string
     * @param username user name
     * @param password password
     */
    public JDBCConnectionCredentials(String driverClassName, String connectionString, 
    	String username, String password) 
    {
    	this.driverClassName = driverClassName;
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
    }

    /**
     * 
     * @return
     */
    public String getDriverClassName() {
		return driverClassName;
	}

	/**
     * Returns the JDBC connection string.
     * @return JDBC connection string
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * Returns the username.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password.
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
