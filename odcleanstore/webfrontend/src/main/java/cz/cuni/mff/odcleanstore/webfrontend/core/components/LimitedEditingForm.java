package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * A form that can only be confirmed by authorized users.
 * @author Jan Michelfeit
 */
public abstract class LimitedEditingForm<T> extends Form<T>
{
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_SUBMIT_BUTTON_ID = "submit";
	
	private boolean isAuthorized;
	
	/**
	 * Constructor.
	 * @param id component ID
	 * @param model component model
	 * @param isAuthorized indicates whether the current user is authorized for submitting the form
	 */
	public LimitedEditingForm(String id, IModel<T> model, boolean isAuthorized) 
	{
		this(id, model, isAuthorized, null);
	}
	
	/**
	 * Constructor.
	 * @param id component ID
	 * @param model component model
	 * @param isAuthorized indicates whether the current user is authorized for submitting the form
	 * @param submitButtonId id of submit button component
	 */
	public LimitedEditingForm(String id, IModel<T> model, boolean isAuthorized, String submitButtonId) 
	{
		super(id, model);
		this.isAuthorized = isAuthorized;
		Component submitButton = new WebMarkupContainer(
				submitButtonId == null ? DEFAULT_SUBMIT_BUTTON_ID : submitButtonId);
		submitButton.setVisible(isAuthorized);
		add(submitButton);
	}
	
	/**
	 * Handler of the on submit event.
	 * Override this method in order to define custom behavior when the form is submitted.
	 */
	protected abstract void onSubmitImpl();
	
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		if (!isAuthorized) 
		{
			tag.put("class", tag.getAttribute("class") + " " + "disabledForm");
		}
	}
	
	@Override
	public boolean isEnabled()
	{
		return isAuthorized;
	}
	
	@Override
	public final void onSubmit()
	{
		if (isAuthorized)
		{
			onSubmitImpl();
		}
	}
}