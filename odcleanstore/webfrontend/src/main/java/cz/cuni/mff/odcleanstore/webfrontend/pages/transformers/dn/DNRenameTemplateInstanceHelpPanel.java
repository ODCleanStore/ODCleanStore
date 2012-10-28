package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Rename-dn-template-instance help panel.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ "PIC" })
public class DNRenameTemplateInstanceHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param compName
	 */
	public DNRenameTemplateInstanceHelpPanel(String compName) 
	{
		super(compName);
	}
}
