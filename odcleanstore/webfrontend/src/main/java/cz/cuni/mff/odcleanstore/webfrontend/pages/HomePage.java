package cz.cuni.mff.odcleanstore.webfrontend.pages;

import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LogInPanel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

/**
 * WebFrontend home page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class HomePage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public HomePage() 
	{
		super("Home", "Welcome to ODCleanStore Administration");
		UserDao userDao = daoLookupFactory.getDao(UserDao.class);
		add(new LogInPanel("logInForm", userDao) 
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return  !ODCSWebFrontendSession.get().isAuthenticated();
			}
		});
	}
}
