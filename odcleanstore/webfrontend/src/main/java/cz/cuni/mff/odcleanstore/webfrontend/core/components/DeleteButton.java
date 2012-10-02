package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * A generic delete button to delete non-raw BOs.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO> The type of the primary BO to be deleted
 */
public class DeleteButton<BO extends EntityWithSurrogateKey> extends AbstractDeleteButton<BO> 
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param compName
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public DeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer boId, String compName,
		String objName, DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, boId, compName, objName, message, redirectPage);
	}
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public DeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer boId, String objName,
		DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		this(dao, boId, createCompName(objName), objName, message, redirectPage);
	}

	
	@Override
	protected void delete() throws Exception 
	{
		dao.delete(boId);
	}
}
