package cz.cuni.mff.odcleanstore.webfrontend.pages.myaccount;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;

public class MyAccountPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(MyAccountPage.class);
	
	//private DaoForEntityWithSurrogateKey<User> userDao;
	
	public MyAccountPage() 
	{
		super(
			"Home > My account > Detail", 
			"Show my account details"
		);
		
		// register page components
		//
		User user = getUser();
		
		addAccountInformationSection(user);
	}

	private User getUser()
	{
		ODCSWebFrontendSession session = ODCSWebFrontendSession.get();
		
		if (!session.isAuthenticated())
			throw new RestartResponseException(LogInPage.class);
		
		return ODCSWebFrontendSession.get().getUser();
	}
	
	private void addAccountInformationSection(User user)
	{
		
		logger.debug("Building account information section for user: [" + user + "].");
		
		setDefaultModel(new CompoundPropertyModel<User>(user));
		
		add(new Label("username"));
		add(new Label("email"));
		add(new Label("firstname"));
		add(new Label("surname"));
	}
}
