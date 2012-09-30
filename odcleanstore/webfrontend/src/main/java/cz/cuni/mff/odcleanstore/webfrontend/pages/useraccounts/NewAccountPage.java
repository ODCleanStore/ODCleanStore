package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import cz.cuni.mff.odcleanstore.configuration.WebFrontendConfig;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.Mail;
import cz.cuni.mff.odcleanstore.webfrontend.util.NewAccountMail;
import cz.cuni.mff.odcleanstore.webfrontend.util.PasswordHandling;

@AuthorizeInstantiation({ Role.ADM })
public class NewAccountPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewAccountPage.class);

	private UserDao userDao;
	
	public NewAccountPage() 
	{
		super(
			"Home > User accounts > New", 
			"Create a new user account"
		);

		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDao(UserDao.class);
		
		// register page components
		//
		addHelpWindow(new UserAccountHelpPanel("content"));
		addNewAccountForm();
	}
		
	private void addNewAccountForm()
	{
		IModel<User> formModel = new CompoundPropertyModel<User>(new User());
		
		Form<User> form = new Form<User>("newAccountForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				User user = this.getModelObject();
				WebFrontendConfig config = NewAccountPage.this.getApp().getConfiguration();
				
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
					logger.error(ex.getMessage());
					getSession().error(ex.getMessage());
					return;
				}
				catch (MessagingException ex)
				{
					logger.error(ex.getMessage());
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
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
