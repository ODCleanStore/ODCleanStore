package cz.cuni.mff.odcleanstore.webfrontend.administration;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;

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
