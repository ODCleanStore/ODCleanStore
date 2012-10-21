package cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions;

/**
 * An abstract parent of all application specific DAO exceptions.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param message
	 */
	public DaoException(String message)
	{
		super(message);
	}
}
