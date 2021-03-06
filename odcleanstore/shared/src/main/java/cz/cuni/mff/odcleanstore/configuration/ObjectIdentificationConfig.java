package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatBoolean;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

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
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "object_identification" + NAME_DELIMITER;

    private Boolean linkWithinGraph;
    private Boolean linkAttachedGraphs;
    private SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials;
    private SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials;

    /**
     * @param linkWithinGraph
     * @param linkAttachedGraphs
     * @param cleanDBSparqlConnectionCredentials
     * @param dirtyDBSparqlConnectionCredentials
     */
    public ObjectIdentificationConfig(Boolean linkWithinGraph, Boolean linkAttachedGraphs,
            SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials,
            SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials) {
        this.linkWithinGraph = linkWithinGraph;
        this.linkAttachedGraphs = linkAttachedGraphs;
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
        ParameterFormat<Boolean> formatBoolean = new FormatBoolean();
        Boolean linkWithinGraph = loadParam(properties, GROUP_PREFIX + "link_within_graph", formatBoolean);
        Boolean linkAttachedGraphs = loadParam(properties, GROUP_PREFIX + "link_attached_graphs", formatBoolean);
        SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials =
                loadSparqlEndpointConnectionCredentials(properties, EnumDbConnectionType.CLEAN, false);
        SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials =
                loadSparqlEndpointConnectionCredentials(properties, EnumDbConnectionType.DIRTY_UPDATE, true);
        return new ObjectIdentificationConfig(linkWithinGraph, linkAttachedGraphs,
                cleanDBSparqlConnectionCredentials, dirtyDBSparqlConnectionCredentials);
    }

    public Boolean isLinkWithinGraph() {
        return linkWithinGraph;
    }
    
    public Boolean isLinkAttachedGraphs() {
    	return linkAttachedGraphs;
    }

    public SparqlEndpointConnectionCredentials getCleanDBSparqlConnectionCredentials() {
        return cleanDBSparqlConnectionCredentials;
    }

    public SparqlEndpointConnectionCredentials getDirtyDBSparqlConnectionCredentials() {
        return dirtyDBSparqlConnectionCredentials;
    }
}
