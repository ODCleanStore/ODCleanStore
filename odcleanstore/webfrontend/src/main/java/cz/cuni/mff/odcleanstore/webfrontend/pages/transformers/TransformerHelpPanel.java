package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

/**
 * Transformer help panel component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.ADM })
public class TransformerHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param compName
	 */
	public TransformerHelpPanel(String compName) 
	{
		super(compName);
	}
}
