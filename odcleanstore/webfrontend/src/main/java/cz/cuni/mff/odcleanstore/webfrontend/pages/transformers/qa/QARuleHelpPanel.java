package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

@AuthorizeInstantiation({ "POC" })
public class QARuleHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public QARuleHelpPanel(String compName) 
	{
		super(compName);
	}
}
