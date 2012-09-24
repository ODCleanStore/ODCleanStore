package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.component.IRequestableComponent;

/**
 * Container of links in the main menu. Controls visibility according to required authorization.
 * 
 * @author Jan Michelfeit
 */
public class MenuGroupComponent extends WebMarkupContainer
{
	private static final long serialVersionUID = 1L;
	
	Class<? extends IRequestableComponent> requiredAuthorizationPage;
	
	/**
	 * Constructor.
	 * @param id component id
	 * @param requiredAuthorizationPage class whose @AuthorizeInstantiation annotation defines the required role
	 */
	public MenuGroupComponent(String id, Class<? extends IRequestableComponent> requiredAuthorizationPage)
	{
		super(id);
		this.requiredAuthorizationPage = requiredAuthorizationPage;
	}
	
	@Override
	public boolean isVisible()
	{
		return getSession().getAuthorizationStrategy().isInstantiationAuthorized(requiredAuthorizationPage);
	}
}
