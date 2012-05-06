package cz.cuni.mff.odcleanstore.webfrontend.administration;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.List;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class AccountsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<User> userDao;
	
	public AccountsListPage() 
	{
		super(
			"Home > Administration > User accounts > List",
			"List all user accounts"
		);
		
		// prepare DAO objects
		//
		userDao = daoLookupFactory.getUserDao();
		
		// register page components
		//
		addUserListView();
	}

	private void addUserListView()
	{
		List<User> allUserAccounts = userDao.loadAll();
		
		ListView<User> listView = new ListView<User>("accountsListTable", allUserAccounts)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<User> item) 
			{
				final User user = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<User>(user));
				
				item.add(new Label("username"));
				item.add(new Label("email"));
				item.add(new Label("firstname"));
				item.add(new Label("surname"));
				
				for (Role role : Role.standardRoles)
					item.add(createRoleLabel(user, role));
				
				item.add(new Link("editPermissionsLink")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() 
					{
						setResponsePage(
							new EditAccountPermissionsPage(user.getId())
						);
					}
				});
			}
		};
		
		add(listView);
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
