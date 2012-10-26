package cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions;

/**
 * Exception thrown when a uniqueness constraint gets violated.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class UniquenessViolationException extends DaoException
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param attributeName
	 */
	public UniquenessViolationException(String attributeName)
	{
		super(
			"The given '" + attributeName + "' value has already been used."
		);
	}
}
