package cz.cuni.mff.odcleanstore.webfrontend.behaviours;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;

/**
 * Javascript Confirmation box.
 * Can be used for confirmation before deletion of a record.
 *
 * Inspired by http://ntsrikanth.blogspot.cz/2008/11/confirmation-dialog.html
 */
public class ConfirmationBoxRenderer extends AbstractBehavior 
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ConfirmationBoxRenderer.class);
	
	/** Message to be desplayed in the confirm box. */
	private String message;
	
	/**
	* Constructor.
	* @param message Message to be shown in the confirm box.
	*/
	public ConfirmationBoxRenderer(final String message) 
	{
		super();
		this.message = message;
	}
	
	/**
	* @param component Component to attach.
	* @param tag Tag to modify.
	* @see org.apache.wicket.behavior.AbstractBehavior#onComponentTag(org.apache.wicket.Component, org.apache.wicket.markup.ComponentTag)
	*/
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag) 
	{
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
