package cz.cuni.mff.odcleanstore.engine.outputws.output;

import java.util.Date;
import java.util.Locale;

/**
 * An abstract base class for query result formatters.
 * @author Jan Michelfeit
 */
public abstract class ResultFormatterBase implements QueryResultFormatter {
	/** Number of milliseconds in a second */
	private static final double SECOND_MS = 1000.0;
	
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
