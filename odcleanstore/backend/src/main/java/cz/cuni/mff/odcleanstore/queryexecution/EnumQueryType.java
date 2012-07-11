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
     * Keyword query.
     * @see QueryExecution#getNamedGraphMetadata()
     */
    NAMED_GRAPH
}
