package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class TransformersConfigurationPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	public TransformersConfigurationPage() 
	{
		super
		(
			"Home > Rules", 
			"Rules configuration"
		);
	}
}
