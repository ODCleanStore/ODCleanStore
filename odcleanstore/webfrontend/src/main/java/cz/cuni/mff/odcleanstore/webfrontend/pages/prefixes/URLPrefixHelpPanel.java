package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

@AuthorizeInstantiation({ Role.ADM })
public class URLPrefixHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public URLPrefixHelpPanel(String compName) 
	{
		super(compName);
	}
}
