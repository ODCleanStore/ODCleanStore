/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public enum InsertResultStatus {
	OK,
	SERVICE_BUSY,
	BAD_CREDENTIALS,
	DUPLICATED_UUID,
	OTHER_ERROR
}
