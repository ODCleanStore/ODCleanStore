/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public interface IScraper {

	// TODO prototype first version authenticate mechanism

	/**
	 * 
	 * @param user
	 * @param password
	 * @return AuthenticateResult
	 */
	public AuthenticateResult authenticate(String user, String password);

	/**
	 * 
	 * @param sessionId
	 * @param metadata
	 * @param triples
	 * @return InsertResult
	 */
	public InsertResult insertWithAutoCommit(String sessionId, Metadata metadata, Triple[] triples);

	/**
	 * 
	 * @param sessionId
	 * @param metadata
	 * @param triples
	 * @return InsertResult
	 */
	public InsertResult beginInsertion(String sessionId, Metadata metadata, Triple[] triples);

	/**
	 * 
	 * @param graphName
	 * @param triples
	 * @return InsertResult
	 */
	public InsertResult moreData(String graphName, Triple[] triples);

	/**
	 * 
	 * @param graphName
	 * @return InsertResult
	 */
	public InsertResult commit(String graphName);
}
