package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Redirect button visible by authorized users only.
 * @author Jan Michelfeit
 */
public class AuthorizedRedirectButton extends RedirectWithParamButton
{
	private static final long serialVersionUID = 1L;
	
	private boolean isAuthorized;
	
	public AuthorizedRedirectButton(Class<? extends FrontendPage> redirectPage, Integer param, boolean isAuthorized, 
		String compName) 
	{
		super(redirectPage, param, compName);
		this.isAuthorized = isAuthorized;
	}
	
	@Override
	public boolean isVisible()
	{
		return isAuthorized;
	}

	@Override
	public void onClick()
	{
		if (isAuthorized)
		{
			super.onClick();
		}
	}
}
