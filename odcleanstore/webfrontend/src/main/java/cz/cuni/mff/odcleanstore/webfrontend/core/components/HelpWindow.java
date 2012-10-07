package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * A generic modal window to display help information.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class HelpWindow extends ModalWindow
{
	private static final long serialVersionUID = 1L;

	private static final int INITIAL_WINDOW_WIDTH = 800;
	private static final int INITIAL_WINDOW_HEIGHT = 600;

	/**
	 * 
	 * @param compName the name of the component
	 * @param content the panel to be displayed inside the window
	 */
	public HelpWindow(String compName, Panel content) 
	{
		super(compName);
		
		setInitialWidth(INITIAL_WINDOW_WIDTH);
		setInitialHeight(INITIAL_WINDOW_HEIGHT);
		
		setContent(content);
	}
	
	@Override
	protected ResourceReference newCssResource()
	{
		return null;
	}
}
