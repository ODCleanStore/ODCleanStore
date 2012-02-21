package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions of ODCleanStore-specific properties and classes.
 * 
 * @author Jan Michelfeit
 */
public final class ODCS {
    /** The namespaspace of the vocabulary as a string. */
    private static final String NS = "http://opendata.cz/infrastructure/odcleanstore/";
    
    /** 
     * Returns the namespaspace of the vocabulary. 
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }
    
    /** Disable constructor for a utility class. */
    private ODCS() {
    }
    
    /* Vocabulary properties: */
    
    /* TODO: return something else than a String? As a Resource or a Property? */

    /**
     * Property linking a named graph to URI of its data source.
     */
    public static final String dataSource = "http://opendata.cz/infrastructure/odcleanstore/dataSource";
    
    /**
     * Property linking a named graph to the date it was stored.
     */
    public static final String stored = "http://opendata.cz/infrastructure/odcleanstore/stored";
    
    /**
     * Property linking a named graph to the publisher of the source data 
     * (not neccessarily their creator).
     */
    public static final String publisher = "http://opendata.cz/infrastructure/odcleanstore/publisher";
    
    /**
     * Property linking a named graph to its error localization score.
     */
    public static final String score = "http://opendata.cz/infrastructure/odcleanstore/score";
    
    /**
     * Property linking a publisher to her aggregated score.
     */
    public static final String publisherScore = "http://opendata.cz/infrastructure/odcleanstore/publisherScore";
    
    /**
     * Property linking a named graph to its conflict resolution quality estimate.
     */
    public static final String quality = "http://opendata.cz/infrastructure/odcleanstore/quality";
}
