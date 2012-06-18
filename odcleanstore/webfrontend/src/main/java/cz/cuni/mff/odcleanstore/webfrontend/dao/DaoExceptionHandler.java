package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

public abstract class DaoExceptionHandler implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public abstract boolean comprisesException(String relevantMessagePart);
	public abstract void handleException(String relevantMessagePart) throws DaoException;
}
