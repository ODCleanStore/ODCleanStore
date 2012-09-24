package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class PipelinesConfigurationPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	public PipelinesConfigurationPage() 
	{
		super
		(
			"Home > Engine", 
			"Engine configuration"
		);
	}
}
