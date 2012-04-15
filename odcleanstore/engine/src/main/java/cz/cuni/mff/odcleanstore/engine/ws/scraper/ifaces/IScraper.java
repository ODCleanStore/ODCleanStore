/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public interface IScraper {
	/**
	 * 
	 * @param user
	 * @param password
	 * @param metadata
	 * @param rdfXmlPayload
	 * @return InsertResult
	 */
	public InsertResult insert(String user, String password, Metadata metadata, String rdfXmlPayload);
}
