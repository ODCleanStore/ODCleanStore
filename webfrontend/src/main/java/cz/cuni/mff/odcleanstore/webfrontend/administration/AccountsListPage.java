package cz.cuni.mff.odcleanstore.webfrontend.administration;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.WicketApplication;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

public class AccountsListPage extends FrontendPage {

	private static final long serialVersionUID = 1L;

	private Dao<User> userDao;
	
	public AccountsListPage() 
	{
		super(
			"Home > Administration > User accounts > List",
			"List all user accounts"
		);

		userDao = ((WicketApplication) WicketApplication.get()).getUserDao();
		
		add(new ListView<User>("accountsListTable", userDao.findAll()) 
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
			}
		});
	}
}
