package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AuthoredEntityLimitedRedirectButton extends RedirectWithParamButton
{
	private static final long serialVersionUID = 1L;
	
	private boolean isAuthorized;
	
	public AuthoredEntityLimitedRedirectButton(Class<? extends FrontendPage> redirectPage, AuthoredEntity entity, 
		String requiredRole, String compName) 
	{
		super(redirectPage, entity.getId(), compName);
		this.isAuthorized = AuthorizationHelper.isAuthorizedForEntityEditing(entity.getAuthorId(), requiredRole);
	}
	
	@Override
	public boolean isVisible()
	{
		return isAuthorized;
	}

	@Override
	public void onClick() 
	{
		if (isAuthorized) {
			super.onClick();
		}
	}
}
