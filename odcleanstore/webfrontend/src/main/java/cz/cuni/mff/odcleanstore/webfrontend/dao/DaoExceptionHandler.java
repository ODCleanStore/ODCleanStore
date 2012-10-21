package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.io.Serializable;

import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

/**
 * An abstract parent of all DAO exception handlers. Exception handlers
 * are used to translate exceptions thrown by Spring JDBC template calls
 * into application specific exceptions.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class DaoExceptionHandler implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns true if and only if the handler is able to handle
	 * the exception with the given relevant message part.
	 * 
	 * @param relevantMessagePart
	 * @return
	 */
	public abstract boolean comprisesException(String relevantMessagePart);
	
	/**
	 * Handles an exception with the given relevant message part.
	 *  
	 * @param relevantMessagePart
	 * @throws DaoException
	 */
	public abstract void handleException(String relevantMessagePart) throws DaoException;
}
