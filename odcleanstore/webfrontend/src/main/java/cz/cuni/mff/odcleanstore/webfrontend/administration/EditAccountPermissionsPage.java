package cz.cuni.mff.odcleanstore.webfrontend.administration;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role.NAME;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class EditAccountPermissionsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<User> userDao;
	
	public EditAccountPermissionsPage(int userId) 
	{
		super(
			"Home > Administration > User accounts > Edit roles", 
			"Edit user account roles"
		);

		// 1. Get the User DAO bean.
		//
		userDao = getApp().getDaoLookupFactory().getUserDao();
		
		// 2. Construct the component hierarchy.
		//
		User user = userDao.load(userId);
		
		setDefaultModel(new CompoundPropertyModel<User>(user));
		
		add(new Label("username"));
		add(new Label("email"));
		
		for (NAME roleName : Role.NAME.values())
			add(createRoleCheckBox(user, roleName.toString()));
	}
	
	private CheckBox createRoleCheckBox(User user, String roleName)
	{
		final boolean userHasRoleAssigned = user.hasRoleAssigned(roleName);
		
		return new CheckBox("role" + roleName, new Model<Boolean>()
		{
			@Override
			public Boolean getObject()
			{
				return userHasRoleAssigned;
			}
		});
	}
}
