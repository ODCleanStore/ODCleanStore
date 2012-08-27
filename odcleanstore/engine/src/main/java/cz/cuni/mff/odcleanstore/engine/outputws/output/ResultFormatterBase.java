package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryType;

import org.restlet.data.CharacterSet;

import java.util.Date;
import java.util.Locale;

/**
 * An abstract base class for query result formatters.
 * @author Jan Michelfeit
 */
public abstract class ResultFormatterBase implements QueryResultFormatter {
    /** Number of milliseconds in a second. */
    private static final double SECOND_MS = 1000.0;
    
    /** Title for a URI query. */
    private static final String TITLE_URI = "URI query for <%s>";

    /** Title for a keyword query. */
    private static final String TITLE_KW = "Keyword query for '%s'";

    /** Title for a metadata query. */
    private static final String TITLE_METADATA = "Metadata query for named graph <%s>";
    
    /** Title for a named graph query. */
    private static final String TITLE_NAMED_GRAPH = "Named graph query for <%s>";

    /** Title for an unknown type of query. */
    private static final String TITLE_GENERAL = "Query %s";
    
    /** Character set for the output. */
    protected static final CharacterSet OUTPUT_CHARSET = CharacterSet.UTF_8;

    /**
     * Format query response title.
     * @param query query
     * @param queryType type of query
     * @return formatted title string
     */
    protected String formatQueryTitle(String query, EnumQueryType queryType) {
        switch (queryType) {
        case KEYWORD:
            return String.format(TITLE_KW, query);
        case METADATA:
            return String.format(TITLE_METADATA, query);
        case NAMED_GRAPH:
            return String.format(TITLE_NAMED_GRAPH, query);
        case URI:
            return String.format(TITLE_URI, query);
        default:
            return String.format(TITLE_GENERAL, query);
        }
    }
    
    /**
     * Format execution time to a readable string.
     * @param executionTime query execution time in milliseconds
     * @return formatted execution time
     */
    protected String formatExecutionTime(long executionTime) {
        return String.format(Locale.ROOT, "%.3f s", executionTime / SECOND_MS);
    }

    /**
     * Format a score/quality to a readable string.
     * @param score score or quality
     * @return formatted score
     */
    protected String formatScore(double score) {
        return String.format(Locale.ROOT, "%.5f", score);
    }

    /**
     * Format a date a readable string.
     * Note: If implementation changes, don't forget to keep it thread-safe.
     * @param date date
     * @return formatted date
     */
    protected String formatDate(Date date) {
        return date.toString();
    }
}
