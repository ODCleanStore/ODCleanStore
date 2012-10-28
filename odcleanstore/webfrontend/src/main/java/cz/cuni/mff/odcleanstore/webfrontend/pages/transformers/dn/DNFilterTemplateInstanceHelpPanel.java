package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Filter-dn-template-instance help panel.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ "PIC" })
public class DNFilterTemplateInstanceHelpPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param compName
	 */
	public DNFilterTemplateInstanceHelpPanel(String compName) 
	{
		super(compName);
	}
}
