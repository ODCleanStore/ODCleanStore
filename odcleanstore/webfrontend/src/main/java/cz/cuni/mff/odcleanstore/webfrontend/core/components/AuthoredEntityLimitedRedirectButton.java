package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AuthoredEntityLimitedRedirectButton extends RedirectWithParamButton
{
	private static final long serialVersionUID = 1L;
	
	private Integer authorId;
	private String requiredRole;
	
	public AuthoredEntityLimitedRedirectButton(Class<? extends FrontendPage> redirectPage, AuthoredEntity entity, 
		String requiredRole, String compName) 
	{
		super(redirectPage, entity.getId(), compName);
		this.authorId = entity.getAuthorId();
		this.requiredRole = requiredRole;
	}
	
	@Override
	public boolean isVisible()
	{
		return AuthorizationHelper.isAuthorizedForEntityEditing(authorId, requiredRole);
	}

	@Override
	public void onClick() 
	{
		if (AuthorizationHelper.isAuthorizedForEntityEditing(authorId, requiredRole)) {
			super.onClick();
		}
	}
}
