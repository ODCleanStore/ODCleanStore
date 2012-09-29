package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AuthoredEntityDeleteButton<BO extends EntityWithSurrogateKey> extends DeleteButton<BO>
{
	private static final long serialVersionUID = 1L;
	
	private boolean isAuthorized;
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AuthoredEntityDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, AuthoredEntity entity, String requiredRole,
		String objName, DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, entity.getId(), objName, message, redirectPage);
		this.isAuthorized = AuthorizationHelper.isAuthorizedForEntityEditing(entity.getAuthorId(), requiredRole);
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
