package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class OutputWSPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	public OutputWSPage() 
	{
		super
		(
			"Home > Output WS", 
			"Output webservice configuration"
		);
	}
}
