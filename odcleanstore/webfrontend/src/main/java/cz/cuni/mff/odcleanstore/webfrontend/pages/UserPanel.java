package cz.cuni.mff.odcleanstore.webfrontend.pages;

import cz.cuni.mff.odcleanstore.util.ArrayUtils;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class UserPanel extends Panel
{
	public UserPanel(String id, Class<? extends Page> logoutPageClass) 
	{
		super(id);
		
		addUsernameLabel();
		addRolesListLabel();
		addLogoutLink(logoutPageClass);
		addMyAccountLink();
		addLoginLink();
	}
		
	private void addUsernameLabel()
	{
		IModel model = new PropertyModel(this, "session.user.username");
		add(new Label("username", model));
	}
	
	private void addRolesListLabel()
	{
		User user = ODCSWebFrontendSession.get().getUser();
		String rolesList = user == null ? "" : formatRolesList(user);
		
		add(new Label("rolesList", rolesList));		
	}
	
	private void addLogoutLink(Class<? extends Page> logoutPageClass)
	{
		PageParameters params = new PageParameters();
		params.add(LogOutPage.REDIRECT_PAGE_PARAM_KEY, logoutPageClass.getName());
		
		add(new BookmarkablePageLink<Object>("logout", LogOutPage.class, params)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return ODCSWebFrontendSession.get().isAuthenticated();
			}
		});
	}
	
	private void addMyAccountLink()
	{
		add(new BookmarkablePageLink<Object>("myAccount", MyAccountPage.class)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return ODCSWebFrontendSession.get().isAuthenticated();
			}
		});
	}
	
	private void addLoginLink()
	{
		add(new Link("login")
		{
			@Override
			public void onClick() 
			{
				throw new RestartResponseAtInterceptPageException(LogInPage.class);
			}
			
			@Override
			public boolean isVisible()
			{
				return !ODCSWebFrontendSession.get().isAuthenticated();
			}
		});
	}

	private String formatRolesList(User user)
	{
		return ArrayUtils.joinArrayItems(user.getRoleLabels(), ", ");
	}
}
