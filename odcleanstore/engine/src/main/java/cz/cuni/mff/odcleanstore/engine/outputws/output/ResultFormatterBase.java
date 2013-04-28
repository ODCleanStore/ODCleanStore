package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryType;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.restlet.data.CharacterSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    
    /** Value factory. */
    protected static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** {@link ODCS#source} as URI. */
    protected static final URI METADATA_SOURCE_PROPERTY = VALUE_FACTORY.createURI(ODCS.source);
    /** {@link ODCS#insertedAt} as URI. */
    protected static final URI METADATA_INSERTED_AT_PROPERTY = VALUE_FACTORY.createURI(ODCS.insertedAt);
    /** {@link ODCS#score} as URI. */
    protected static final URI METADATA_SCORE_PROPERTY = VALUE_FACTORY.createURI(ODCS.score);
    /** {@link ODCS#license} as URI. */
    protected static final URI METADATA_LICENCES_PROPERTY = VALUE_FACTORY.createURI(ODCS.license);
    /** {@link ODCS#updateTag} as URI. */
    protected static final URI METADATA_UPDATE_TAG_PROPERTY = VALUE_FACTORY.createURI(ODCS.updateTag);
    /** {@link ODCS#publishedBy} as URI. */
    protected static final URI METADATA_PUBLISHED_BY_PROPERTY = VALUE_FACTORY.createURI(ODCS.publishedBy);
    /** {@link ODCS#publisherScore} as URI. */
    protected static final URI METADATA_PUBLISHER_SCORE_PROPERTY = VALUE_FACTORY.createURI(ODCS.publisherScore);

    private final DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
    
    /**
     * Returns a {@link DateFormat} for formatting of time in output.
     * @return a {@link DateFormat} instance
     */
    private DateFormat getTimeFormat() {
        return timeFormat;
    }
    
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
     * Format score to a readable string.
     * @param value Value representing a double literal
     * @return formatted date
     */
    protected String formatScore(Value value) {
        if (!(value instanceof Literal)) {
            return "";
        }
        try {
            double score = ((Literal) value).doubleValue();
            return formatScore(score);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    /**
     * Format a date a readable string.
     * @param date date
     * @return formatted date
     */
    protected String formatDate(Date date) {
        return getTimeFormat().format(date);
    }
    
    /**
     * Format a date a readable string.
     * @param value Value representing a date literal
     * @return formatted date
     */
    protected String formatDate(Value value) {
        if (!(value instanceof Literal)) {
            return "";
        }
        try {
            Date date = ((Literal) value).calendarValue().toGregorianCalendar().getTime();
            return formatDate(date);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
