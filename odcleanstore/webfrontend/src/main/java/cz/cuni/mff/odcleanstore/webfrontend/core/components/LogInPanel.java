package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.security.NoSuchAlgorithmException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

/**
 * Container panel for the login form.
 * @author Jan Michelfeit
 */
public class LogInPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public LogInPanel(String id, DaoForEntityWithSurrogateKey<User> userDao) {
		super(id);
		add(new LogInForm("form", userDao));
	}
}

/**
 * Login form.
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 */
class LogInForm extends Form<User>
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(LogInForm.class);
	
	private DaoForEntityWithSurrogateKey<User> userDao;
	
	private String username;
	private String password;
	
	public LogInForm(String id, DaoForEntityWithSurrogateKey<User> userDao) 
	{
		super(id);
		
		this.userDao = userDao;
		
		setModel(new CompoundPropertyModel(this));
		
		add(new TextField<String>("username"));
		add(new PasswordTextField("password"));
	}
	
	@Override
	public void onSubmit()
	{
		// 1. Load the User DB entry associated with the given username.
		//
		User user;
		try {
			user = userDao.loadBy("username", username);
			if (user == null)
				throw new Exception();
		}
		catch (Exception ex)
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
		
		ODCSWebFrontendSession.get().setUser(user);
		
		getSession().info("User successfuly logged in.");
		boolean redirected = continueToOriginalDestination();
		if (!redirected) 
		{
			setResponsePage(getApplication().getHomePage());
		}
	}
}