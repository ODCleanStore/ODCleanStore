package cz.cuni.mff.odcleanstore.webfrontend.behaviours;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;

/**
 * A javascript-based confirmation box used to ask the user
 * to confirm delete operations.
 *
 * Inspired by http://ntsrikanth.blogspot.cz/2008/11/confirmation-dialog.html
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 */
public class ConfirmationBoxRenderer extends Behavior 
{
	private static final long serialVersionUID = 1L;
	
	//private static Logger logger = Logger.getLogger(ConfirmationBoxRenderer.class);
	
	/** the message to be displayed in the confirmation box */
	private String message;
	
	/**
	 * 
	* @param message Message to be shown in the confirm box.
	*/
	public ConfirmationBoxRenderer(final String message) 
	{
		super();
		this.message = message;
	}
	
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag) 
	{
		// appends the javascript code to handle to confirmation message box
		// directly to the onclick attribute of the component
		//
		if (component instanceof Button || component instanceof Link) 
		{
			String originalOnclick = tag.getAttributes().getString("onclick"); 
			tag.getAttributes().remove("onclick");
			tag.getAttributes().put(
				"onclick", 
				"if (confirm('" + message + "')) { " + originalOnclick + " } else { return false; }"
			);
		}
	}
}
