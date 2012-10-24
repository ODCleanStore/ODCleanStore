package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

/**
 * Link visible and clickable only for authorized users.
 */
public abstract class AuthorizedLink<T> extends Link<T>
{
	private static final long serialVersionUID = 1L;
	
	private boolean isAuthorized;

	public AuthorizedLink(String componentId, boolean isAuthorized)
	{
		super(componentId);
		this.isAuthorized = isAuthorized;
	}
	
	public AuthorizedLink(final String componentId, IModel<T> model, boolean isAuthorized)
	{
		super(componentId, model);
		this.isAuthorized = isAuthorized;
	}

	/**
	 * Link visibility. 
	 * This method is final, override {@link #isVisibleAuthorized()} in order to customize visibility. 
	 */
	@Override
	public final boolean isVisible()
	{
		return isAuthorized && isVisibleAuthorized();
	}

	/**
	 * Link visibility 
	 * @return visibility
	 */
	protected boolean isVisibleAuthorized() 
	{
		return true;
	}

	/**
	 * Action performed on click. 
	 * This method is final, override {@link #onClickAuthorized()} in order to customize behavior. 
	 */
	@Override
	public final void onClick()
	{
		if (isAuthorized) 
		{
			onClickAuthorized();
		}
	}

	/**
	 * Action performed on click.
	 */
	abstract protected void onClickAuthorized();
}
