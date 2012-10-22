package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.UniquenessViolationException;

/**
 * Handles DAO exceptions thrown when trying to insert a row which would
 * violate a uniqueness constraint.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class UniquenessViolationHandler extends DaoExceptionHandler 
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean comprisesException(String relevantMessagePart)
	{
		return relevantMessagePart.startsWith("SR175:");
	}

	@Override
	public void handleException(String relevantMessagePart) throws UniquenessViolationException 
	{
		assert 
			relevantMessagePart.startsWith(
				"SR175: Uniqueness violation : Violating unique index DB_ODCLEANSTORE_"
			);
	
		String[] tokens = relevantMessagePart.split(" ");
		String constraintName = tokens[7];
		
		String[] parts = constraintName.split("_");
		String attrName = parts[parts.length - 1];
		
		throw new UniquenessViolationException(attrName);
	}
}
