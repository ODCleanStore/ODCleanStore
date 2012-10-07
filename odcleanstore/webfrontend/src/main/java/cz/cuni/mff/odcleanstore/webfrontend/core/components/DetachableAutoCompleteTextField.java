package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * 
 * @author Tomáš Soukup
 *
 */
public class DetachableAutoCompleteTextField extends AutoCompleteTextField<String> 
{
	private static final long serialVersionUID = 215039015274338419L;
	
	private IModel<List<String>> choicesModel;
	
	public DetachableAutoCompleteTextField(String componentName, IModel<List<String>> choicesModel)
	{
		super(componentName);
		this.choicesModel = choicesModel;
	}
	
	public DetachableAutoCompleteTextField(
			String componentName, AutoCompleteSettings settings, IModel<List<String>> choicesModel)
	{
		super(componentName, settings);
		this.choicesModel = choicesModel;
	}

	@Override
	protected Iterator<String> getChoices(String input) 
	{	
		List<String> allChoices = choicesModel.getObject();
		List<String> matchingChoices = new ArrayList<String>();
		for (String choice: allChoices)
		{
			if (choice.contains(input))
			{
				matchingChoices.add(choice);
			}
		}
		return matchingChoices.iterator();
	}
	
	@Override
	protected void onDetach()
	{
		choicesModel.detach();
		super.onDetach();
	}
	
	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		response.renderCSSReference(new PackageResourceReference(
			AutoCompleteTextField.class, "DefaultCssAutoCompleteTextField.css"));
	}
}
