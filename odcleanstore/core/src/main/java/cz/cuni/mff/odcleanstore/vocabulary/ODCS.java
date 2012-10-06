package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions of ODCleanStore-specific properties and classes.
 *
 * @author Jan Michelfeit
 */
public final class ODCS {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://opendata.cz/infrastructure/odcleanstore/";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private ODCS() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF

    /**
     * Property linking a named graph to its quality assessment score.
     */
    public static final String score = NS + "score";
    
    /**
     * Property linking a named graph to its quality assessment score trace.
     */
    public static final String scoreTrace = NS + "scoreTrace";

    /**
     * Property storing original values of particular property
     */
    public static final String propertyEndpointsBackup = NS + "propertyEndpointsBackup";

    /**
     * Property specifying subject of a property backup
     */
    public static final String propertySubjectBackup = NS + "propertySubjectBackup";

    /**
     * Property specifying object of a property backup
     */
    public static final String propertyObjectBackup = NS + "propertyObjectBackup";

    /**
     * Property linking a publisher to its aggregated score.
     */
    public static final String publisherScore = NS + "publisherScore";

    /**
     * Property linking a named graph to its conflict resolution quality estimate.
     */
    public static final String quality = NS + "quality";
    
    /**
     * Property linking a data graph to its metadata graph. 
     */
    public static final String metadataGraph = NS + "metadataGraph";
    
    /**
     * Property linking a data graph to its provenance metadata graph. 
     */
    public static final String provenanceMetadataGraph = 
    		NS + "provenanceMetadataGraph";
    
    /**
     * Property linking a data graph to its attached graphs added by transformers. 
     */
    public static final String attachedGraph = NS + "attachedGraph";
    
    /**
     * Class of resources describing a result of a query. 
     */
    public static final String queryResponse = NS + "QueryResponse";
    
    /**
     * A query over the output webservice. 
     */
    public static final String query = NS + "query";
    
    /**
     * Property linking an instance of odcs:QueryResponse to named graphs containing a result triple. 
     */
    public static final String result = NS + "result";
    
    /**
     * Number of quads in a query result. 
     */
    public static final String totalResults = NS + "totalResults";
    
    /**
     * Property linking a data graph to a Quality Assessment rule it violates. 
     */
    public static final String violatedQARule = NS + "violatedQARule";
    
    /**
     * Coefficient of a Quality Assessment rule.
     */
    public static final String coefficient = NS + "coefficient";

    /**
     * Property linking a query result triple (the named graph it is placed in, respectively) to
     * named graphs the triple was selected or calculated from.
     */
    public static final String sourceGraph = NS + "sourceGraph";
    
    /**
     * @see W3P#insertedAt
     */
    public static final String insertedAt = NS + "insertedAt";
    
    /**
     * @see W3P#insertedBy
     */
    public static final String insertedBy = NS + "insertedBy";
    
    /**
     * @see W3P#publishedBy
     */
    public static final String publishedBy = NS + "publishedBy";

    /**
     * @see W3P#source
     */
    public static final String source = NS + "source";
    
    /**
     * @see DC#license
     */
    public static final String license = NS + "license";
    
    /**
     * Class of resources describing a Quality Assessment rule. 
     */
    public static final String QARule = NS + "QARule";
    
    /**
     * Base for relative URI in input data. 
     */
    public static final String dataBaseUrl = NS + "dataBaseUrl";
    
    // CHECKSTYLE:ON
}
