package cz.cuni.mff.odcleanstore.webfrontend.administration;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role.NAME;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

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

		// 1. Get the User DAO bean.
		//
		userDao = getApp().getDaoLookupFactory().getUserDao();
		
		// 2. Construct the component hierarchy.
		//
		add(new ListView<User>("accountsListTable", userDao.loadAll()) 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<User> item) 
			{
				User user = item.getModelObject();
				item.setModel(new CompoundPropertyModel<User>(user));

				item.add(new Label("username"));
				item.add(new Label("email"));
				item.add(new Label("createdAt"));
				
				for (NAME roleName : Role.NAME.values())
					item.add(createRoleLabel(user, roleName.toString()));
			}
		});
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
	private Label createRoleLabel(User user, String roleName)
	{	
		for (Role role : user.getRoles())
		{
			if (roleName.equals(role.getName()))
				return new Label("role" + roleName, "X");
		}
		
		Label label = new Label("role" + roleName, "&nbsp;");
		label.setEscapeModelStrings(false);
		
		return label;
	}
}
