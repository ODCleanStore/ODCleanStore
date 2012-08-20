/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

/**
 * 
 * Helper class for processing some formats.
 * 
 *  @author Petr Jerman
 */
public class FormatHelper {

	private static final String ERROR_FORMAT_EXCEPTION = "Error format exception message";
	private static final String ODCS_NAMESPACE = "cz.cuni.mff.odcleanstore";
	private final static SimpleDateFormat SIMPLEDATEFORMAT_W3CDTF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Transform date to W3CDTF format.
	 * 
	 * @param date
	 *            The time value to be formatted into a W3CDTF format.
	 * @return The formatted string.
	 */
	public static String getTypedW3CDTF(Date date) {
		String formattedDate = SIMPLEDATEFORMAT_W3CDTF.format(date);

		StringBuilder result = new StringBuilder();
		result.append('"');
		result.append(formattedDate, 0, formattedDate.length() - 2);
		result.append(':');
		result.append(formattedDate, formattedDate.length() - 2, formattedDate.length());
		result.append("\"^^<");
		result.append(XMLSchema.dateTimeType);
		result.append('>');
		return result.toString();
	}

	/**
	 * Get current date in W3CDTF format.
	 * 
	 * @return The formatted string.
	 */
	public static String getTypedW3CDTFCurrent() {
		return getTypedW3CDTF(new Date());
	}
	
	public static String formatExceptionForLog(Throwable exception, String firstMessage, Object... args) {
		String message = firstMessage;
		try {
			StringBuilder sb = new StringBuilder();
			if (args.length > 0) {
				firstMessage = String.format(firstMessage, args);
			}
			sb.append(firstMessage);
			sb.append("\n        ");
			sb.append(ERROR_FORMAT_EXCEPTION);
			sb.append('\n');
			message = sb.toString();
			
			sb = new StringBuilder();
			HashSet<String> rows = new HashSet<String>(); 
			sb.append(firstMessage);
			while(exception != null) {
				sb.append("\n        ");
				sb.append(exception.getClass().getSimpleName());
				sb.append(" - ");
				sb.append(exception.getMessage());
				for(int i = exception.getStackTrace().length - 1; i >=0 ; i--) {
					StackTraceElement st = exception.getStackTrace()[i];
					String row = st.toString();
					if(row.startsWith(ODCS_NAMESPACE) && rows.add(row)) {
							sb.append("\n              ");
							sb.append(row);
					}
				}
				exception = exception.getCause();
			}
			sb.append('\n');
			return sb.toString();
		} catch(Exception ie) {
			return ERROR_FORMAT_EXCEPTION  + message;
		}
	}

	public static String formatGraphMessage(String message, String graphUuid, Object... args) {
		try {
			return String.format("Graph %s - %s", graphUuid, String.format(message, args));
		} catch(Exception ie) {
			return  ERROR_FORMAT_EXCEPTION + message;
		}
	}
}
