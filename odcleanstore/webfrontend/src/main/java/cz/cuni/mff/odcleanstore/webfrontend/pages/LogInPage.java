package cz.cuni.mff.odcleanstore.webfrontend.pages;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LogInPanel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

/**
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class LogInPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param pageCrumbs
	 * @param pageTitle
	 */
	public LogInPage() throws AssertionError 
	{
		super("Home > LogIn", "Log in");

		DaoForEntityWithSurrogateKey<User> userDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(UserDao.class);
		add(new LogInPanel("logInForm", userDao));
	}
}