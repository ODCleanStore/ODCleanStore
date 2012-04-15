/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class InsertResult {
	public InsertResultStatus status;

	public InsertResult() {
	}

	public InsertResult(InsertResultStatus status) {
		this.status = status;
	}
}
