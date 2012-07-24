package cz.cuni.mff.odcleanstore.engine.inputws.ifaces;

/**
 *  @author Petr Jerman
 */
public interface IInputWS {
	/**
	 * 
	 * @param user
	 * @param password
	 * @param metadata
	 * @param payload
	 * @throws InsertException 
	 */
	public void insert(String user, String password, Metadata metadata, String payload) throws InsertException;
}
