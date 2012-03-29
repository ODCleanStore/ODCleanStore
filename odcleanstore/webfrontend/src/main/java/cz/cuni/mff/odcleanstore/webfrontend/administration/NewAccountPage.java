package cz.cuni.mff.odcleanstore.webfrontend.administration;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

@AuthorizeInstantiation({ "ADM" })
public class NewAccountPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<User> userDao;
	
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
				
				user.setCreatedAtToNow();
				
				userDao.insert(user);
				
				getSession().info("The user account was successfuly created.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		add(newAccountForm);
		
		newAccountForm.add(new TextField<String>("username"));
		newAccountForm.add(new TextField<String>("email"));
		
	}

}
