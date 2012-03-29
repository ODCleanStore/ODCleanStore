package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

public class UserPanel extends Panel
{
	public UserPanel(String id, Class<? extends Page> logoutPageClass) 
	{
		super(id);
		
		// 1. Add the username label.
		//
		add(new Label("username", new PropertyModel(this, "session.user.username")));
		
		// 2. Add the roles-list label.
		//
		User user = WicketSession.get().getUser();
		String rolesList = user == null ? "" : getRolesList(user);
		
		add(new Label("rolesList", rolesList));		
		
		// 3. Add the log-out link.
		//
		PageParameters params = new PageParameters();
		params.add(LogOutPage.REDIRECT_PAGE_PARAM_KEY, logoutPageClass.getName());
		
		add(new BookmarkablePageLink<Object>("logout", LogOutPage.class, params)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return WicketSession.get().isAuthenticated();
			}
		});
		
		// 4. Add the log-in link.
		//
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
				return !WicketSession.get().isAuthenticated();
			}
		});
	}
	
	private String getRolesList(User user)
	{
		String result = "";
		for (Role role : user.getRoles())
			result += role.getName() + ", ";
		
		if (result.endsWith(", "))
			result = result.substring(0, result.length() - 2);
		
		return result;
	}
}
