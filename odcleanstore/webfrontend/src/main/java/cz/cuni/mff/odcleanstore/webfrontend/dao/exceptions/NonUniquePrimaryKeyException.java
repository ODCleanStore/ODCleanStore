package cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions;

public class NonUniquePrimaryKeyException extends DaoException
{
	private static final long serialVersionUID = 1L;

	public NonUniquePrimaryKeyException()
	{
		super(
			"The given record is not unique."
		);
	}
}
