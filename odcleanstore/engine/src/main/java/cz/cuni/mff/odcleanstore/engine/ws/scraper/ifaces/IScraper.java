package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 *  @author Petr Jerman
 */
public interface IScraper {
	/**
	 * 
	 * @param user
	 * @param password
	 * @param metadata
	 * @param rdfXmlPayload
	 * @throws InsertException 
	 */
	public void insert(String user, String password, Metadata metadata, String rdfXmlPayload) throws InsertException;
}
