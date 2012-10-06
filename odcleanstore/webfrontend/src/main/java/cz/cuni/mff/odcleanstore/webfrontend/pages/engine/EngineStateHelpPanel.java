package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class EngineStateHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public EngineStateHelpPanel(String compName) 
	{
		super(compName);
	}
}