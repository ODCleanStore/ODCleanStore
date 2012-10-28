package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;

/**
 * DB-output help page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class DBOutputHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param compName
	 */
	public DBOutputHelpPanel(String compName) 
	{
		super(compName);
	}
}
