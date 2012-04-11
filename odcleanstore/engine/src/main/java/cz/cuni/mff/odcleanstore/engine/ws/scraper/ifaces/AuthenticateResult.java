/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class AuthenticateResult {

	public AuthenticateResultStatus status;
	public String sessionId;
	public int maxMessageLengthInBytes;

	public AuthenticateResult() {
	}

	public AuthenticateResult(AuthenticateResultStatus status, String sessionId, int maxMessageLengthInBytes) {
		this.status = status;
		this.sessionId = sessionId;
		this.maxMessageLengthInBytes = maxMessageLengthInBytes;
	}
}
