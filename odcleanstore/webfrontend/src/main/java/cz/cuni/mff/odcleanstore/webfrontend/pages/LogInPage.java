package cz.cuni.mff.odcleanstore.webfrontend.pages;

import cz.cuni.mff.odcleanstore.webfrontend.core.components.LogInPanel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

/**
 * WebFrontend log-in page.
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
		super("Home > Log In", "Log in");

		UserDao userDao = daoLookupFactory.getDao(UserDao.class);
		add(new LogInPanel("logInForm", userDao));
	}
}