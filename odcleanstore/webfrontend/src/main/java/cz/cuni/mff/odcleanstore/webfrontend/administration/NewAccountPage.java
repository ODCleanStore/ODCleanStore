package cz.cuni.mff.odcleanstore.webfrontend.administration;

import java.security.NoSuchAlgorithmException;

import javax.mail.MessagingException;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.util.Mail;
import cz.cuni.mff.odcleanstore.webfrontend.util.NewAccountMail;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

@AuthorizeInstantiation({ "ADM" })
public class NewAccountPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<User> userDao;
	
	// TODO: to be put into a configuration object parsed from
	// a configuration file
	private String gmailAddress = "odcleanstore@gmail.com";
	private String gmailPassword = "odcleanstore2012";
	
	public NewAccountPage() 
	{
		super(
			"Home > Administration > User accounts > Create", 
			"Create a new user account"
		);

		// 1. Get the User DAO bean.
		//
		userDao = getApp().getDaoLookupFactory().getUserDao();
		
		// 2. Construct the component hierarchy.
		//
		Form<User> newAccountForm = new Form<User>("newAccountForm", new CompoundPropertyModel<User>(new User()))
		{
			@Override
			protected void onSubmit() 
			{
				User user = this.getModelObject();
				
				String password;
				try {
					password = generatePasswordHash(user, gmailAddress, gmailPassword);
				}
				catch (Exception ex)
				{
					// TODO: log exception message
					getSession().error("The user account could not be created.");
					setResponsePage(AccountsListPage.class);
					
					return;
				}
	
				user.setCreatedAtToNow();
				user.setPassword(password);
				
				userDao.insert(user);
				
				getSession().info("The user account was successfuly created.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		add(newAccountForm);
		
		newAccountForm.add(new TextField<String>("username"));
		newAccountForm.add(new TextField<String>("email"));
		
	}

	/**
	 * Generate a password, send it through gmail to the email-address related to
	 * the given user-account (using the given gmail account credentials) and
	 * return an MD5 hash of it.
	 * 
	 * @param user
	 * @param gmailAddr
	 * @param gmailPasswd
	 * @return
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 */
	private String generatePasswordHash(User user, String gmailAddr, String gmailPasswd) 
		throws MessagingException, NoSuchAlgorithmException
	{
		// 1. Generate plain-text password.
		//
		String password = PasswordHandling.generateRandomPassword(
			PasswordHandling.DEFAULT_CHARSET,
			PasswordHandling.DEFAULT_LENGTH
		);
		
		// 2. Send confirmation email.
		//
		try 
		{
			Mail email = new NewAccountMail(user, password);
			email.sendThroughGmail(gmailAddr, gmailPasswd);
		} 
		catch (MessagingException e) 
		{
			throw new MessagingException(
				"Could not send confirmation email to: " + user.getEmail()
			);
		}
		
		// 3. Hash plain-text password using MD5.
		//
		String hash;
		try {
			hash = PasswordHandling.calculateMD5Hash(password);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new NoSuchAlgorithmException("Could not calculate password hash.");
		}
		
		return hash;
	}
}
