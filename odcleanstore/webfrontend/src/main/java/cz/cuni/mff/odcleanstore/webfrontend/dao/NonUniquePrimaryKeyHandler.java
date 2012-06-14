package cz.cuni.mff.odcleanstore.webfrontend.dao;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.NonUniquePrimaryKeyException;

public class NonUniquePrimaryKeyHandler extends DaoExceptionHandler
{
	private Logger logger = Logger.getLogger(NonUniquePrimaryKeyHandler.class);
	
	@Override
	public boolean comprisesException(String relevantMessagePart) 
	{
		logger.debug("Inside comprises");
		
		return relevantMessagePart.startsWith("SR197:");
	}

	@Override
	public void handleException(String relevantMessagePart) throws DaoException 
	{
		logger.debug("Inside handle");
		
		assert 
			relevantMessagePart.startsWith(
				"SR197: Non unique primary key on DB.ODCLEANSTORE."
			);
	
		throw new NonUniquePrimaryKeyException();
	}
}
