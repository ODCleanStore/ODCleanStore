package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

/**
 * Encapsulates Object-Identification configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Object-Identification configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ObjectIdentificationConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "object_identification";
    }

    private URI linksGraphURIPrefix;
    private SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials;
    private SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials; 

    /**
     * @param linksGraphURIPrefix
     * @param cleanSparqlEndpointUrl
     * @param dirtySparqlEndpointUrl
     * @param dirtySparqlEndpointUsername
     * @param dirtySparqlEndpointPassword
     */
    public ObjectIdentificationConfig(URI linksGraphURIPrefix, 
    		SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials,
    		SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials) {
        this.linksGraphURIPrefix = linksGraphURIPrefix;
        this.cleanDBSparqlConnectionCredentials = cleanDBSparqlConnectionCredentials;
        this.dirtyDBSparqlConnectionCredentials = dirtyDBSparqlConnectionCredentials;
    }

    /**
     * Extracts Object-Identification configuration values from the given Properties instance.
     * Returns a ObjectIdentificationConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static ObjectIdentificationConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI linksGraphURIPrefix = loadParam(properties, "links_graph_uri_prefix", formatURI);
        SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials = loadCleanDbCredentials(properties);
        SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials = loadDirtyDbCredentials(properties);
        
        return new ObjectIdentificationConfig(
        		linksGraphURIPrefix, cleanDBSparqlConnectionCredentials, dirtyDBSparqlConnectionCredentials);
    }
    
    private static SparqlEndpointConnectionCredentials loadCleanDbCredentials(Properties properties) 
    		throws ParameterNotAvailableException, IllegalParameterFormatException {
        URL url = loadParam(properties, "clean_sparql_endpoint_url", new FormatURL());

        return new SparqlEndpointConnectionCredentials(url);
    }
    
    private static SparqlEndpointConnectionCredentials loadDirtyDbCredentials(Properties properties) 
    		throws ParameterNotAvailableException, IllegalParameterFormatException {
        URL url = loadParam(properties, "dirty_sparql_endpoint_url", new FormatURL());
        String username = loadParam(properties, "dirty_sparql_endpoint_username", new FormatString());
        String password = loadParam(properties, "dirty_sparql_endpoint_password", new FormatString());

        return new SparqlEndpointConnectionCredentials(url, username, password);
    }

    /**
     *
     * @return
     */
    public URI getLinksGraphURIPrefix() {
        return linksGraphURIPrefix;
    }

	public SparqlEndpointConnectionCredentials getCleanDBSparqlConnectionCredentials() {
		return cleanDBSparqlConnectionCredentials;
	}

	public SparqlEndpointConnectionCredentials getDirtyDBSparqlConnectionCredentials() {
		return dirtyDBSparqlConnectionCredentials;
	}
}
