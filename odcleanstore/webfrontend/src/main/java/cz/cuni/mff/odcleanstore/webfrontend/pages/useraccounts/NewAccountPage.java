package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import java.security.NoSuchAlgorithmException;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
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
	
	private DaoForEntityWithSurrogateKey<User> userDao;
	
	public NewAccountPage() 
	{
		super(
			"Home > User accounts > Account > Create", 
			"Create a new user account"
		);

		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(UserDao.class);
		
		// register page components
		//
		addNewAccountForm();
	}
	
	private void addNewAccountForm()
	{
		IModel formModel = new CompoundPropertyModel<User>(new User());
		
		Form<User> form = new Form<User>("newAccountForm", formModel)
		{
			@Override
			protected void onSubmit()
			{
				User user = this.getModelObject();
				Configuration config = NewAccountPage.this.getApp().getConfiguration();
				
				try 
				{
					String password = PasswordHandling.generatePassword();
					String salt = PasswordHandling.generateSalt();
					String passwordHash = PasswordHandling.calculatePasswordHash(password, salt);
					
					user.setPasswordHash(passwordHash);
					user.setSalt(salt);
					
					Mail mail = new NewAccountMail(user, password);
					
					userDao.save(
						user, 
						new SendConfirmationEmailSnippet(config, mail)
					);
				}
				catch (DaoException ex) 
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (MessagingException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					getSession().error("The user account could not be created due to an unexpected error.");
					return;
				}
								
				getSession().info("The user account was successfuly created.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		form.add(createTextfield("username"));
		addEmailTextfield(form);
		form.add(createTextfield("firstname", false));
		form.add(createTextfield("surname", false));

		add(form);
	}

	private void addEmailTextfield(Form<User> form)
	{
		TextField<String> textField = new TextField<String>("email");
		
		textField.setRequired(true);
		textField.add(EmailAddressValidator.getInstance());

		form.add(textField);
	}
}
