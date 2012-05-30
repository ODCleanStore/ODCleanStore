package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import java.security.NoSuchAlgorithmException;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.Mail;
import cz.cuni.mff.odcleanstore.webfrontend.util.NewAccountMail;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class NewAccountPage extends FrontendPage
{
	private static Logger logger = Logger.getLogger(NewAccountPage.class);
	
	private Dao<User> userDao;
	
	public NewAccountPage() 
	{
		super(
			"Home > User accounts > Create", 
			"Create a new user account"
		);

		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDao(UserDao.class);
		
		// register page components
		//
		addNewAccountForm();
	}
	
	private void addNewAccountForm()
	{
		IModel formModel = new CompoundPropertyModel<User>(new User());
		
		Form<User> newAccountForm = new Form<User>("newAccountForm", formModel)
		{
			@Override
			protected void onSubmit()
			{
				User user = this.getModelObject();
				Configuration config = NewAccountPage.this.getApp().getConfiguration();
				
				try 
				{
					initNewPasswordForUser(user, config);
					userDao.save(user);
				} 
				catch (Exception ex) 
				{
					getSession().error(ex.getMessage());
					setResponsePage(AccountsListPage.class);
				}
								
				getSession().info("The user account was successfuly created.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		addUsernameTextfield(newAccountForm);
		addEmailTextfield(newAccountForm);
		addFirstnameTextfield(newAccountForm);
		addSurnameTextfield(newAccountForm);

		add(newAccountForm);
	}
	
	private void addUsernameTextfield(Form<User> form)
	{
		TextField<String> textField = new TextField<String>("username");
		
		textField.setRequired(true);

		form.add(textField);
	}
	
	private void addEmailTextfield(Form<User> form)
	{
		TextField<String> textField = new TextField<String>("email");
		
		textField.setRequired(true);

		form.add(textField);
	}

	private void addFirstnameTextfield(Form<User> form)
	{
		TextField<String> textField = new TextField<String>("firstname");
		
		textField.setRequired(true);

		form.add(textField);
	}

	private void addSurnameTextfield(Form<User> form)
	{
		TextField<String> textField = new TextField<String>("surname");
		
		textField.setRequired(true);

		form.add(textField);
	}
	
	private void initNewPasswordForUser(User user, Configuration config) 
		throws MessagingException, NoSuchAlgorithmException
	{
		logger.debug("Initializing password for user: " + user.getId());
		
		// 1. Generate random plain-text password.
		//
		logger.debug("Generating random password.");
		
		String password = PasswordHandling.generateRandomString(
			PasswordHandling.DEFAULT_CHARSET,
			PasswordHandling.DEFAULT_PASSWORD_LENGTH
		);
		
		// 2. Generate random salt.
		//
		logger.debug("Generating random salt.");
		
		String salt = PasswordHandling.generateRandomString(
			PasswordHandling.DEFAULT_CHARSET,
			PasswordHandling.DEFAULT_SALT_LENGTH
		);
		
		// 3. Send confirmation email.
		//
		logger.debug("Sending confirmation email.");
		
		try 
		{
			Mail email = new NewAccountMail(user, password);
			email.sendThroughGmail(config.getGmailAddress(), config.getGmailPassword());
		} 
		catch (MessagingException ex) 
		{
			throw new MessagingException(
				"Could not send confirmation email to: " + user.getEmail()
			);
		}
		
		// 4. Hash plain-text password using MD5.
		//
		logger.debug("Calculating password hash.");
		
		String passwordHash;
		try 
		{
			passwordHash = PasswordHandling.calculatePasswordHash(password, salt);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new NoSuchAlgorithmException("Could not calculate password hash.");
		}

		// 5. Update the passwordHash and salt of the given user.
		//
		user.setPasswordHash(passwordHash);
		user.setSalt(salt);
	}
}
