package cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions;

public class UniquenessViolationException extends DaoException
{
	private static final long serialVersionUID = 1L;

	public UniquenessViolationException(String attributeName)
	{
		super(
			"Field '" + attributeName + "' must contain a unique value."
		);
	}
}
