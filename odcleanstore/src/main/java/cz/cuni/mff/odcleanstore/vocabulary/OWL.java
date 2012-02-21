package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions for OWL.
 * 
 * @author Jan Michelfeit
 */
public final class OWL {
    /** The namespaspace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2002/07/owl#";
    
    /** 
     * Returns the namespaspace of the vocabulary. 
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }
    
    /** Disable constructor for a utility class. */
    private OWL() {
    }
    
    /* Vocabulary properties: */
    
    public static final String sameAs = "http://www.w3.org/2002/07/owl#sameAs";
    
    public static final String maxCardinality = "http://www.w3.org/2002/07/owl#maxCardinality";
    
    public static final String minCardinality = "http://www.w3.org/2002/07/owl#minCardinality";
    
    public static final String cardinality = "http://www.w3.org/2002/07/owl#cardinality";
    
}
