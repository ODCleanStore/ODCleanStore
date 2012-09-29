package cz.cuni.mff.odcleanstore.webfrontend.pages.myaccount;

import java.security.NoSuchAlgorithmException;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;
import cz.cuni.mff.odcleanstore.webfrontend.validators.OldPasswordValidator;

public class EditPasswordPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(EditPasswordPage.class);
	
	private UserDao userDao;
	
	public EditPasswordPage() 
	{
		super(
			"Home > My account > Edit", 
			"Edit my password"
		);
		
		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDao(UserDao.class);
		
		// register page components
		//
		User user = getUser();
		
		addChangePasswordForm(user);
	}

	private User getUser()
	{
		ODCSWebFrontendSession session = ODCSWebFrontendSession.get();
		
		if (!session.isAuthenticated())
			throw new RestartResponseException(LogInPage.class);
		
		return ODCSWebFrontendSession.get().getUser();
	}
	
	private void addChangePasswordForm(User user)
	{
		add(new ChangePasswordForm("changePasswordForm", userDao, user));
	}
}

class ChangePasswordForm extends Form<ChangePasswordForm>
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(ChangePasswordForm.class);

	private UserDao userDao;
	private User user;
	
	@SuppressWarnings("unused")
	private String oldPassword;
	private String newPassword;
	@SuppressWarnings("unused")
	private String newPasswordAgain;

	/**
	 * 
	 * @param id
	 * @param userDao
	 * @param user
	 */
	public ChangePasswordForm(String id, UserDao userDao, User user) 
	{
		super(id);
		
		this.userDao = userDao;
		this.user = user;
		
		setModel(new CompoundPropertyModel<ChangePasswordForm>(this));
		
		add(createOldPasswordField(user));

		PasswordTextField newPassword = new PasswordTextField("newPassword");
		add(newPassword);
		
		PasswordTextField newPasswordAgain = new PasswordTextField("newPasswordAgain");
		add(newPasswordAgain);

		add(new EqualInputValidator(newPassword, newPasswordAgain));
	}
	
	@Override
	public void onSubmit()
	{		
		// 1. Calculate the passwordHash based on the given plain-text password
		// and the associated salt.
		//
		String passwordHash;
		
		try 
		{
			String salt = user.getSalt();
			passwordHash = PasswordHandling.calculatePasswordHash(newPassword, salt);
		}
		catch (NoSuchAlgorithmException ex) 
		{
			getSession().error("Could not change the password due to an unexpected error.");
			setResponsePage(EditPasswordPage.class);
			
			return;
		}
		
		// 2. Set the newly computed password hash.
		//
		user.setPasswordHash(passwordHash);
		
		// 3. Update the user data in the DB.
		//
		try {
			userDao.update(user);
		} 
		catch (Exception ex)
		{
			// TODO: log the error
			
			getSession().error(
				"Could not change the password due to an unexpected error."
			);
			
			return;
		}
		
		// 4. Update the user object in the session.
		//
		ODCSWebFrontendSession.get().setUser(user);
		
		getSession().info("Password successfuly changed.");
		setResponsePage(MyAccountPage.class);
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	private PasswordTextField createOldPasswordField(User user)
	{
		PasswordTextField field = new PasswordTextField("oldPassword");
		
		field.add(new OldPasswordValidator(user.getPasswordHash(), user.getSalt()));
		
		return field;
	}
}
