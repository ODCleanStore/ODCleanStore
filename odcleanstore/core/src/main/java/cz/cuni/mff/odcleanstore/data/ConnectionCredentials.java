package cz.cuni.mff.odcleanstore.data;

/**
 * SPARQL endpoint representation. User name and password are optional.
 * Immutable.
 *
 * @author Tomas Soukup
 */
public class ConnectionCredentials {

    /** The URI of the SPARQL endpoint. */
    private String uri;

    /** User name required for authentication. */
    private String username = null;

    /** Password required for authentication. */
    private String password = null;

    /**
     * Create a new instance with the given SPARQL endpoint URI.
     * @param uri URI of the SPARQL endpoint
     */
    public ConnectionCredentials(String uri) {
        this.uri = uri;
    }

    /**
     * Create a new instance with the given SPARQL endpoint URI and credentials.
     * @param uri URI of the SPARQL endpoint
     * @param username SPARQL endpoint username
     * @param password SPARQL endpoint password
     */
    public ConnectionCredentials(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns URI of the SPARQL endpoint.
     * @return URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns user name required for authentication.
     * @return user name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns password required for authentication.
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
