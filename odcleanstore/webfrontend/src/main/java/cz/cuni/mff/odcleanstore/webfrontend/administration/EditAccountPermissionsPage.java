package cz.cuni.mff.odcleanstore.webfrontend.administration;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role.NAME;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

@AuthorizeInstantiation({ "ADM" })
public class EditAccountPermissionsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<User> userDao;
	private Dao<Role> roleDao;
	
	private HashMap<Role.NAME, Boolean> rolesSettings;
	
	public EditAccountPermissionsPage(final int userId) 
	{
		super(
			"Home > Administration > User accounts > Edit roles", 
			"Edit user account roles"
		);

		// 1. Get the DAO beans.
		//
		DaoLookupFactory daoLookupFactory = getApp().getDaoLookupFactory();
		
		userDao = daoLookupFactory.getUserDao();
		roleDao = daoLookupFactory.getRoleDao();
		
		// 2. Load the target User instance.
		final User user = userDao.load(userId);

		// 3. Construct the component hierarchy.
		//
		setDefaultModel(new CompoundPropertyModel<User>(user));
		
		add(new Label("username"));
		add(new Label("email"));
		
		Form<User> editPermissionsForm = new Form<User>("editPermissionsForm")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				user.removeAllRoles();
				
				List<Role> roles = roleDao.loadAll();
				for (Role role : roles)
				{
					Role.NAME roleName = Role.NAME.valueOf(role.getName());
					if (rolesSettings.get(roleName))
						user.addRole(role);
				}
				
				userDao.update(user);
				
				getSession().info("User permissions were successfuly modified.");
				setResponsePage(AccountsListPage.class);
			}
		};
		
		add(editPermissionsForm);
		
		rolesSettings = new HashMap<Role.NAME, Boolean>();
		clearRolesSettings(user);
		
		for (final NAME roleName : Role.NAME.values())
		{
			IModel model = new Model<Boolean>()
			{ 
				@Override
				public Boolean getObject()
				{
					return rolesSettings.get(roleName);
				}
				
				@Override
				public void setObject(Boolean value)
				{
					rolesSettings.put(roleName, value);
				}
			};
			
			editPermissionsForm.add(new CheckBox("role" + roleName, model));
		}
		
		Button resetButton = new Button("resetButton")
		{
			@Override
			public void onSubmit()
			{
				setResponsePage(
					new EditAccountPermissionsPage(userId)
				);
			}
		};
		resetButton.setDefaultFormProcessing(false);
		
		editPermissionsForm.add(resetButton);
	}
	
	private void clearRolesSettings(User user)
	{
		for (Role.NAME roleName : Role.NAME.values())
			rolesSettings.put(roleName, user.hasRoleAssigned(roleName.toString()));
	}
}
