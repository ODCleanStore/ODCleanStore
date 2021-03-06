package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

/**
 * Help panel for ontologies.
 * @author Jakub Daniel
 */
@AuthorizeInstantiation({ Role.ONC })
public class OntologyHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public OntologyHelpPanel(String compName) 
	{
		super(compName);
	}
}
