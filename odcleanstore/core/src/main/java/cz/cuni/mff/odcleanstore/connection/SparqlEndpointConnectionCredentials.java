package cz.cuni.mff.odcleanstore.connection;

import java.net.URL;

/**
 * Encapsulates the connection coordinates of a SPARQL Endpoint.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class SparqlEndpointConnectionCredentials {

    private URL url;
    private String username;
    private String password;

    /**
     * Creates a new instance.
     * @param url SPARQL endpoint URL
     */
    public SparqlEndpointConnectionCredentials(URL url) {
        this(url, null, null);
    }
    
    /**
     * Creates a new instance.
     * @param url
     * @param username
     * @param password
     */
    public SparqlEndpointConnectionCredentials(URL url, String username, String password) {
    	this.url = url;
    	this.username = username;
    	this.password = password;
    }

    /**
     * Returns the SPARQL endpoint URL.
     * @return SPARQL endpoint URL
     */
    public URL getUrl() {
        return url;
    }
    
    /**
     * Returns the SPARQL endpoint username.
     * @return SPARQL endpoint username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Returns the SPARQL endpoint password.
     * @return SPARQL endpoint password
     */
    public String getPassword() {
        return password;
    }
}
