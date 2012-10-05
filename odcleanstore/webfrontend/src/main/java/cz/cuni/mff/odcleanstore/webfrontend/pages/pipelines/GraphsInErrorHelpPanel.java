package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

@AuthorizeInstantiation({ Role.PIC })
public class GraphsInErrorHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public GraphsInErrorHelpPanel(String compName) 
	{
		super(compName);
	}
}