package cz.cuni.mff.odcleanstore.webfrontend;

import java.security.NoSuchAlgorithmException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.administration.AccountsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

public class LogInPage extends FrontendPage
{
	public LogInPage() 
	{
		super("Home > LogIn", "Log in");
		
		UserDao userDao = (UserDao) getApp().getDaoLookupFactory().getUserDao();
		add(new LogInForm("logInForm", userDao));
	}
}

class LogInForm extends Form
{
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
		User user;
		
		try 
		{
			user = userDao.load(
				username, 
				PasswordHandling.calculateMD5Hash(password)
			);
		}
		catch (NoSuchAlgorithmException ex) 
		{
			getSession().error("Could not authenticate the user.");
			setResponsePage(getApplication().getHomePage());
			
			return;
		}
		
		if (user == null)
		{
			getSession().error("The given credentails are invalid.");
			setResponsePage(LogInPage.class);
			
			return;
		}
		
		WicketSession.get().setUser(user);
		
		getSession().info("User successfuly logged in.");
		setResponsePage(getApplication().getHomePage());
	}
}
