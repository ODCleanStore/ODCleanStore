package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class UserAccountsPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public UserAccountsPage() 
	{
		super("Home > User accounts", "Management of user accounts");
	}
}
