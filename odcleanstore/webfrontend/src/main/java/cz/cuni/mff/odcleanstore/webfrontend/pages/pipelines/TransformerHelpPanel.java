package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

@AuthorizeInstantiation({ "POC" })
public class TransformerHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public TransformerHelpPanel(String compName) 
	{
		super(compName);
	}
}
