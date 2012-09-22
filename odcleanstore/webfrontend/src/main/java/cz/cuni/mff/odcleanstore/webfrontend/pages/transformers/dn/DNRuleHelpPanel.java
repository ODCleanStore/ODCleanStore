package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

@AuthorizeInstantiation({ "PIC" })
public class DNRuleHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public DNRuleHelpPanel(String compName) 
	{
		super(compName);
	}
}
