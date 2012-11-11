package cz.cuni.mff.odcleanstore.webfrontend.pages;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.pages.myaccount.MyAccountPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.ArrayUtils;

/**
 * A panel which contains basic information about the currently logged-in
 * user.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class UserPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param id
	 * @param logoutPageClass
	 */
	public UserPanel(String id, Class<? extends Page> logoutPageClass) 
	{
		super(id);
		
		add(createUsernameLabel());
		add(createRolesListLabel());
		add(createLogoutLink(logoutPageClass));
		add(createMyAccountLink());
		add(createLoginLink());
	}
	
	/**
	 * Returns a label to display the username of the currently logged-in
	 * user.
	 * 
	 * @return
	 */
	private Label createUsernameLabel()
	{
		IModel<String> model = new PropertyModel<String>(this, "session.user.username");
		return new Label("username", model);
	}
	
	/**
	 * Returns a label to display a list of all roles assigned to the
	 * currently logged-in user.
	 *  
	 * @return
	 */
	private Label createRolesListLabel()
	{
		User user = ODCSWebFrontendSession.get().getUser();
		String rolesList = user == null ? "" : formatRolesList(user);
		
		return new Label("rolesList", rolesList);		
	}
	
	/**
	 * Returns a link component to log-out the currently logged-in user.
	 * 
	 * @param logoutPageClass
	 * @return
	 */
	private BookmarkablePageLink<Object> createLogoutLink(Class<? extends Page> logoutPageClass)
	{
		PageParameters params = new PageParameters();
		params.add(LogOutPage.REDIRECT_PAGE_PARAM_KEY, logoutPageClass.getName());
		
		return new BookmarkablePageLink<Object>("logout", LogOutPage.class, params)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return ODCSWebFrontendSession.get().isAuthenticated();
			}
		};
	}
	
	/**
	 * Returns a link component to redirect to the user-account page.
	 * 
	 * @return
	 */
	private BookmarkablePageLink<Object> createMyAccountLink()
	{
		return new BookmarkablePageLink<Object>("myAccount", MyAccountPage.class)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible()
			{
				return ODCSWebFrontendSession.get().isAuthenticated();
			}
		};
	}
	
	/**
	 * Returns a link component to redirect to the log-in page.
	 * 
	 * @return
	 */
	private Link<String> createLoginLink()
	{
		return new Link<String>("login")
		{
			private static final long serialVersionUID = 1L;

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
		};
	}

	/**
	 * Joins the labels of all roles assigned to the given user concatenated
	 * to a single String.
	 * 
	 * @param user
	 * @return
	 */
	private String formatRolesList(User user)
	{
		return ArrayUtils.joinArrayItems(user.getRoleLabels(), ", ");
	}
}
