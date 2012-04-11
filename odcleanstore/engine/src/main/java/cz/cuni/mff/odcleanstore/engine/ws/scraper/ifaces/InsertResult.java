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
	public String graphName;

	public InsertResult() {
	}

	public InsertResult(InsertResultStatus status, String graphName) {
		this.status = status;
	}
}
