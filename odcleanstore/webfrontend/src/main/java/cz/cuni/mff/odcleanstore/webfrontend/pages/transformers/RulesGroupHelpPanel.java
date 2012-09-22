package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

@AuthorizeInstantiation({ "PIC" })
public class RulesGroupHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public RulesGroupHelpPanel(String compName) 
	{
		super(compName);
	}
}
