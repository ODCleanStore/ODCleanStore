/**
 * 
 */
package cz.odcleanstorage.prototype.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * Helper class for processing some data formats.
 * 
 * @author Petr Jerman (petr.jerman@centrum.cz)
 * 
 */
public class DataFormat {

	private static SimpleDateFormat SimpleDateFormat_W3CDTF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Transform date to W3CDFT format
	 */
	public static String getW3CDTF(Date date) {
		return  "\"" + SimpleDateFormat_W3CDTF.format(date) + "\"";
	}

	/**
	 * Get current date in W3CDFT format
	 */
	public static String getW3CDFTCurrent() {
		return "\"" + SimpleDateFormat_W3CDTF.format(new Date()) + "\"";
	}
	
	/**
	 * Get <http://opendata.cz/TripleGroups/random/{uuid}> uri
	 */
	public static String generateOpenDataTripleGroupsRandomUuidUri() {
		return "<http://opendata.cz/TripleGroups/random/" + UUID.randomUUID().toString() +">";
	}
}
