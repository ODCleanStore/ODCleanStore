package cz.cuni.mff.odcleanstore.webfrontend.administration;

import java.util.Arrays;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.dao.User;

public class AccountsListPage extends FrontendPage {

	private static final long serialVersionUID = 1L;

	public AccountsListPage() {
		super("Home > Administration > User accounts > List",
				"List all user accounts");

		add(new ListView<User>("accountsListTable",
				Arrays.asList(getApp().users)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<User> item) {
				User user = item.getModelObject();
				item.setModel(new CompoundPropertyModel<User>(user));

				item.add(new Label("username"));
				item.add(new Label("email"));
				item.add(new Label("createdAt"));
			}
		});
	}
}
