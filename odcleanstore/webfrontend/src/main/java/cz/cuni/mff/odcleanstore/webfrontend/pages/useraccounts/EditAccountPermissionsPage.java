package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.RoleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Edit-account-permissions (e.g. assigned roles) page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.ADM })
public class EditAccountPermissionsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private UserDao userDao;
	private RoleDao roleDao;

	/**
	 * 
	 * @param userId
	 */
	public EditAccountPermissionsPage(final Integer userId) 
	{
		super(
			"Home > User accounts > Edit roles", 
			"Edit user account roles"
		);
		
		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDao(UserDao.class);
		roleDao = daoLookupFactory.getDao(RoleDao.class);
		
		// prepare the target User instance
		//
		final User user = userDao.load(userId);
		
		// register page components
		//
		addHelpWindow(new UserAccountHelpPanel("content"));
		
		setDefaultModel(new CompoundPropertyModel<User>(user));
		
		add(new Label("username"));
		add(new Label("email"));
		add(new Label("firstname"));
		add(new Label("surname"));
		
		add(new UserPermissionsForm("editPermissionsForm", userId, userDao, roleDao));
	}
}

/**
 * Edit-account-permissions form component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
class UserPermissionsForm extends Form<UserPermissionsForm>
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(UserPermissionsForm.class);
	
	private UserDao userDao;
	private RoleDao roleDao;

	private Integer userId;
	
	private Map<Role, Boolean> currentRolesSettings;
	
	/**
	 * 
	 * @param id
	 * @param userDao
	 * @param roleDao
	 */
	public UserPermissionsForm(String id, Integer userId,
		UserDao userDao, RoleDao roleDao) 
	{
		super(id);
		
		this.userId = userId;
		
		this.userDao = userDao;
		this.roleDao = roleDao;
		
		resetRolesSettings(userId);
		
		for (final Role role : Role.getStandardRoles())
			addRoleCheckBox(role);
		
		add(
			new RedirectWithParamButton
			(
				EditAccountPermissionsPage.class, 
				userId, 
				"resetButton"
			)
		);
	}

	@Override
	protected void onSubmit()
	{
		User user = userDao.load(userId);
		
		setUpRoles(user);
		
		try {
			userDao.update(user);
		} 
		catch (DaoException ex)
		{
			logger.error(ex.getMessage(), ex);
			getSession().error(ex.getMessage());
			return;
		}
		catch (Exception ex) 
		{
			logger.error("Could not edit user permissions due to: " + ex.getMessage());
			
			getSession().error(
				"User permissions could not be modified due to an unexpected error."
			);
			
			return;
		}
		
		getSession().info("User permissions were successfuly modified.");
		setResponsePage(AccountsListPage.class);
	}
	
	/**
	 * 
	 * @param user
	 */
	private void setUpRoles(User user)
	{
		user.removeAllRoles();
		
		List<Role> roles = roleDao.loadAll();
		for (Role role : roles)
		{
			if (currentRolesSettings.get(role))
				user.addRole(role);
		}
	}
	
	/**
	 * 
	 * @param userId
	 */
	private void resetRolesSettings(final Integer userId)
	{
		currentRolesSettings = new HashMap<Role, Boolean>();
		
		User user = userDao.load(userId);
		Set<Role> assignedRoles = user.getRoles();
		
		for (Role role : Role.getStandardRoles())
			currentRolesSettings.put(role, assignedRoles.contains(role));
	}

	/**
	 * 
	 * @param role
	 */
	private void addRoleCheckBox(final Role role)
	{
		IModel<Boolean> model = new Model<Boolean>()
		{ 
			private static final long serialVersionUID = 1L;

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
		
		add(new CheckBox(roleLabel, model));
	}
}
