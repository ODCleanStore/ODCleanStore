package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

@AuthorizeInstantiation({ Role.PIC })
public class DBOutputHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public DBOutputHelpPanel(String compName) 
	{
		super(compName);
	}
}
