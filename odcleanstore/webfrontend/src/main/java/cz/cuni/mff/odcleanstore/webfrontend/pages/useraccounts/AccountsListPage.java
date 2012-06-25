package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class AccountsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<User> userDao;
	
	public AccountsListPage() 
	{
		super(
			"Home > User accounts > List",
			"List all user accounts"
		);
		
		// prepare DAO objects
		//
		userDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(UserDao.class);
		
		// register page components
		//
		addAccountsListTable();
	}

	private void addAccountsListTable()
	{
		IDataProvider<User> data = new DataProvider<User>(userDao);
		
		DataView<User> dataView = new DataView<User>("accountsListTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) 
			{
				User user = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<User>(user));
				
				item.add(new Label("username"));
				item.add(new Label("email"));
				item.add(new Label("firstname"));
				item.add(new Label("surname"));
				
				for (Role role : Role.standardRoles)
					item.add(createRoleLabel(user, role));
				
				item.add(
					new DeleteButton<User>
					(
						userDao,
						user.getId(),
						"user",
						new DeleteConfirmationMessage("user"),
						AccountsListPage.this
					)
				);
				
				item.add(
					createGoToPageButton
					(
						EditAccountPermissionsPage.class, 
						user.getId(), 
						"managePermissions"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	/**
	 * Returns the correct label for the given user and the given role
	 * (e.g. returns an empty label if the user does not have the role
	 * assigned and an crossline if he does).
	 * 
	 * @param user
	 * @param roleName
	 * @return
	 */
	private Label createRoleLabel(User user, Role role)
	{	
		String roleLabel = "role" + role.getLabel();
		
		if (user.hasAssignedRole(role))
			return new Label(roleLabel, "X");
		
		Label label = new Label(roleLabel, "&nbsp;");
		label.setEscapeModelStrings(false);
		
		return label;
	}
}
