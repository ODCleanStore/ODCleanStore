package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.RoleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class EditAccountPermissionsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(EditAccountPermissionsPage.class);
	
	private DaoForEntityWithSurrogateKey<User> userDao;
	private DaoForEntityWithSurrogateKey<Role> roleDao;
	
	private Map<Role, Boolean> currentRolesSettings;
	
	public EditAccountPermissionsPage(final Long userId) 
	{
		super(
			"Home > User accounts > Edit roles", 
			"Edit user account roles"
		);
		
		// prepare DAO objects
		//
		userDao = (DaoForEntityWithSurrogateKey<User>) daoLookupFactory.getDao(UserDao.class);
		roleDao = (DaoForEntityWithSurrogateKey<Role>) daoLookupFactory.getDao(RoleDao.class);
		
		// prepare the target User instance
		//
		final User user = userDao.load(userId);
		
		// register page components
		//
		setDefaultModel(new CompoundPropertyModel<User>(user));
		
		add(new Label("username"));
		add(new Label("email"));
		add(new Label("firstname"));
		add(new Label("surname"));
		
		addEditPermissionsForm(user);
	}
	
	private void addEditPermissionsForm(final User user)
	{
		Form<User> form = new Form<User>("editPermissionsForm")
		{
			@Override
			protected void onSubmit()
			{
				// TODO: obalit transakci
				
				user.removeAllRoles();
				
				List<Role> roles = roleDao.loadAll();
				for (Role role : roles)
				{
					if (currentRolesSettings.get(role))
						user.addRole(role);
				}
				
				try {
					userDao.update(user);
				} 
				catch (Exception e) 
				{
					getSession().error(
						"User permissions could not be modified due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("User permissions were successfuly modified.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		addRolesCheckBoxes(form, user);

		addResetButton(form, user);

		add(form);
	}
	
	private void addRolesCheckBoxes(Form<User> form, User user)
	{
		prepareRolesSettings(user);
		
		for (final Role role : Role.standardRoles)
			addRoleCheckBox(form, role);
	}
	
	private void prepareRolesSettings(User user)
	{
		currentRolesSettings = new HashMap<Role, Boolean>();
		
		Set<Role> assignedRoles = user.getRoles();
		for (Role role : Role.standardRoles)
			currentRolesSettings.put(role, assignedRoles.contains(role));
	}
	
	private void addRoleCheckBox(Form<User> form, final Role role)
	{
		IModel model = new Model<Boolean>()
		{ 
			@Override
			public Boolean getObject()
			{
				return currentRolesSettings.get(role);
			}
			
			@Override
			public void setObject(Boolean value)
			{
				currentRolesSettings.put(role, value);
			}
		};

		String roleLabel = "role" + role.getLabel();
		
		form.add(new CheckBox(roleLabel, model));
	}
	
	private void addResetButton(Form<User> form, final User user)
	{
		Button resetButton = new Button("resetButton")
		{
			@Override
			public void onSubmit()
			{
				setResponsePage(
					new EditAccountPermissionsPage(user.getId())
				);
			}
		};

		resetButton.setDefaultFormProcessing(false);
		
		form.add(resetButton);
	}

}
