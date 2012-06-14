package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.UniquenessViolationException;

public class UniquenessViolationHandler extends DaoExceptionHandler
{

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
