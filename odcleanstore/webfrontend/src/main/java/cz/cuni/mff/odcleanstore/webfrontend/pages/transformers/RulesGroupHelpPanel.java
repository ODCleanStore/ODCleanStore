package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

/**
 * Group-of-rules help panel.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class RulesGroupHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param compName
	 */
	public RulesGroupHelpPanel(String compName) 
	{
		super(compName);
	}
}
