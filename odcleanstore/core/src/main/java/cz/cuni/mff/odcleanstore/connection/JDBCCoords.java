package cz.cuni.mff.odcleanstore.connection;

import java.net.URL;

/**
 * Encapsulates the coordinates of a JDBC connection.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class JDBCCoords {
    private URL url;
    private String username;
    private String password;

    /**
     * Creates a new instance.
     * @param url JDBC connection string
     * @param username user name
     * @param password password
     */
    public JDBCCoords(URL url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the JDBC connection string.
     * @return JDBC connection string
     */
    public URL getUrl() {
        return url;
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
