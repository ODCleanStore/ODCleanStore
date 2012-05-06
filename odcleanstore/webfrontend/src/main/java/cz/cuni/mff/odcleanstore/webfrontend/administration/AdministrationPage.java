package cz.cuni.mff.odcleanstore.webfrontend.administration;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation({ "ADM" })
public class AdministrationPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AdministrationPage() 
	{
		super("Home > Administration", "Administration");
	}
}
