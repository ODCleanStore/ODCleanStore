package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class UserPanel extends Panel
{
	public UserPanel(String id, Class<? extends Page> logoutPageClass) 
	{
		super(id);
		
		add(new Label("username", new PropertyModel<String>(this, "session.user.username")));
		
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
}
