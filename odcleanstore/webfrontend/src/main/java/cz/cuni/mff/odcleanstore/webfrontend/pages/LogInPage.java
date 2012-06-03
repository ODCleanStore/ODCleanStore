package cz.cuni.mff.odcleanstore.webfrontend.pages;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.WicketSession;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class LogInPage extends FrontendPage
{
	private UserDao userDao;
	
	/**
	 * 
	 * @param pageCrumbs
	 * @param pageTitle
	 */
	public LogInPage() throws AssertionError 
	{
		super("Home > LogIn", "Log in");

		userDao = (UserDao) daoLookupFactory.getUnsafeDao(UserDao.class);
		
		addLoginForm();
	}
	
	private void addLoginForm()
	{
		add(new LogInForm("logInForm", userDao));
	}

}

/**
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
class LogInForm extends Form
{
	private static Logger logger = Logger.getLogger(LogInForm.class);
	
	private UserDao userDao;
	
	private String username;
	private String password;
	
	public LogInForm(String id, UserDao userDao) 
	{
		super(id);
		
		this.userDao = userDao;
		
		setModel(new CompoundPropertyModel<LogInForm>(this));
		
		add(new TextField("username"));
		add(new PasswordTextField("password"));
	}
	
	@Override
	public void onSubmit()
	{
		// 1. Load the User DB entry associated with the given username.
		//
		User user = userDao.loadForUsername(username);
		if (user == null)
		{
			getSession().error("The given username is not registered.");
			setResponsePage(LogInPage.class);
			
			return;
		}
		
		// 2. Calculate the passwordHash based on the given plain-text password
		// and the associated salt.
		//
		String passwordHash;
		
		try 
		{
			String salt = user.getSalt();
			passwordHash = PasswordHandling.calculatePasswordHash(password, salt);
		}
		catch (NoSuchAlgorithmException ex) 
		{
			getSession().error("Could not authenticate the user.");
			setResponsePage(getApplication().getHomePage());
			
			return;
		}
		
		// 3. Compare the calculated and the associated passwordHashes.
		//
		if (!passwordHash.equals(user.getPasswordHash()))
		{
			getSession().error("The given password is invalid.");
			setResponsePage(LogInPage.class);
			
			return;
		}
		
		WicketSession.get().setUser(user);
		
		getSession().info("User successfuly logged in.");
		setResponsePage(getApplication().getHomePage());
	}
}
