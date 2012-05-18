package cz.cuni.mff.odcleanstore.connection;

import java.net.URL;

/**
 * Encapsulates the connection coordinates of a SPARQL Endpoint.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class SparqlEndpointCoords {
    // note that the endpoint connection coordinates currently currently consist of
    // a single URL; it is still a good idea to encapsulate this fact inside a custom
    // class though, for this is a detail that can change in future
    //
    private URL url;

    /**
     * Creates a new instance.
     * @param url SPARQL endpoint URL
     */
    public SparqlEndpointCoords(URL url) {
        this.url = url;
    }

    /**
     * Returns the SPARQL endpoint URL.
     * @return SPARQL endpoint URL
     */
    public URL getUrl() {
        return url;
    }
}
