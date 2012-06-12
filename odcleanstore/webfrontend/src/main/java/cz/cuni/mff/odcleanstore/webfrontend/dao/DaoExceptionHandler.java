package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

public abstract class DaoExceptionHandler 
{	
	public abstract boolean comprisesException(String relevantMessagePart);
	public abstract void handleException(String relevantMessagePart) throws DaoException;
}
