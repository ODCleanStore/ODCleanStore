package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Delete button visible by authorized users only.
 * @author Jan Michelfeit
 *
 * @param <BO>
 */
public class AuthorizedDeleteButton<BO extends EntityWithSurrogateKey> extends DeleteButton<BO>
{
	private static final long serialVersionUID = 1L;
	
	protected final boolean isAuthorized;
	
	/**
	 * @param dao
	 * @param entity
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AuthorizedDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, AuthoredEntity entity, String objName,
		DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, entity.getId(), objName, message, redirectPage);
		this.isAuthorized = AuthorizationHelper.isAuthorizedForEntityEditing(entity.getAuthorId());
	}
	
	/**
	 * @param dao
	 * @param param
	 * @param isAuthorized
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AuthorizedDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer param, boolean isAuthorized,
		String objName,	DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, param, objName, message, redirectPage);
		this.isAuthorized = isAuthorized;
	}
	
	/**
	 * @param dao
	 * @param param
	 * @param isAuthorized
	 * @param compName
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AuthorizedDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer param, boolean isAuthorized, String compName,
		String objName, DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, param, compName, objName, message, redirectPage);
		this.isAuthorized = isAuthorized;
	}

	@Override
	public boolean isVisible()
	{
		return isAuthorized;
	}
	
	@Override
	protected void delete() throws Exception 
	{
		if (isAuthorized)
		{
			super.delete();
		}
	}
}
