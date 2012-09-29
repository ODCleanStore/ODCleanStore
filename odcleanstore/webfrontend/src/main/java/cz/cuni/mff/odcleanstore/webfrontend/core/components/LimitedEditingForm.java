package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * @author Jan Michelfeit
 */
public abstract class LimitedEditingForm<T> extends Form<T>
{
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_SUBMIT_BUTTON_ID = "submit";
	
	private boolean isAuthorized;
	
	public LimitedEditingForm(String id, IModel<T> model, boolean isAuthorized) 
	{
		this(id, model, isAuthorized, null);
	}
	
	public LimitedEditingForm(String id, IModel<T> model, boolean isAuthorized, String submitButtonId) 
	{
		super(id, model);
		this.isAuthorized = isAuthorized;
		Component submitButton = new WebMarkupContainer(submitButtonId == null ? DEFAULT_SUBMIT_BUTTON_ID : submitButtonId);
		submitButton.setVisible(isAuthorized);
		add(submitButton);
	}
	
	protected abstract void onSubmitImpl();
	
	@Override
	public final void onSubmit()
	{
		if (isAuthorized)
		{
			onSubmitImpl();
		}
	}
}