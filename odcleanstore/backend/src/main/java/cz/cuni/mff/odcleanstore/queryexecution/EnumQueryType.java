package cz.cuni.mff.odcleanstore.queryexecution;

/**
 * Type of query.
 * @author Jan Michelfeit
 */
public enum EnumQueryType {
    /**
     * URI query.
     * @see QueryExecution#findURI()
     * */
    URI,

    /**
     * Keyword query.
     * @see QueryExecution#findKeyword()
     */
    KEYWORD,

    /**
     * Named graph metadata query.
     * @see QueryExecution#getNamedGraphMetadata()
     */
    METADATA
}
