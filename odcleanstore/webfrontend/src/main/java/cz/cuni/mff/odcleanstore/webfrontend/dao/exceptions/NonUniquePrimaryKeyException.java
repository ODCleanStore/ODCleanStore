package cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions;

/**
 * Exception thrown when a primary key constraint gets violated.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class NonUniquePrimaryKeyException extends DaoException
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NonUniquePrimaryKeyException()
	{
		super(
			"The given record is not unique."
		);
	}
}
