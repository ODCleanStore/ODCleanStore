package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class TruncatedLabel extends Label
{
	private static final long serialVersionUID = 1L;
	
	private int numOfCharacters;
	
	public TruncatedLabel(String id, int numOfCharacters) 
	{
		super(id);

		this.numOfCharacters = numOfCharacters;
	}

	public TruncatedLabel(final String id, IModel<?> model, int numOfCharacters)
	{
		super(id, model);
		this.numOfCharacters = numOfCharacters;
	}

	public TruncatedLabel(final String id, String label, int numOfCharacters)
	{
		this(id, new Model<String>(label), numOfCharacters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		String labelText = getDefaultModelObjectAsString();
		String truncatedText = truncateText(labelText);
		
		replaceComponentTagBody(markupStream, openTag, truncatedText);
	}
	
	private String truncateText(String text)
	{
		if (text.length() <= numOfCharacters)
			return text;
		
		return text.substring(0, numOfCharacters - 1) + " ...";
	}
}
