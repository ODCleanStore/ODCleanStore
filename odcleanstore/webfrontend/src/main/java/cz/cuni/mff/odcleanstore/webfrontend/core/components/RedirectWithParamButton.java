package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * TODO: change documentation comments (plus classname, aso.) to reflect the 
 * fact that this no longer is a "redirect with a single param button"
 *  
 * A generic button to redirect to another page wich takes a single Integer 
 * parameter (e.g. the id of a BE) as a constructor argument.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RedirectWithParamButton extends Link<String>
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(RedirectWithParamButton.class);
	
	/** the class of the page to redirect to */
	private Class<? extends FrontendPage> redirectPage;
	
	/** the constructor arguments of the page */
	private Object[] params;
	
	/**
	 * 
	 * @param redirectPage
	 * @param param
	 * @param compName
	 */
	public RedirectWithParamButton(final Class<? extends FrontendPage> redirectPage, 
		final Integer param, final String compName) 
	{
		super(compName);

		this.redirectPage = redirectPage;
		this.params = new Object[]{param};
	}
	
	public RedirectWithParamButton(final Class<? extends FrontendPage> redirectPage,
		final String compName, final Object... param) 
	{
		super(compName);

		this.redirectPage = redirectPage;
		this.params = param;
	}

	@Override
	public void onClick() 
	{
		FrontendPage page;
		
		try 
		{
			// using reflection here (instead of passing the page instance as an costructor
			// argument) is necessary in order to postpone creating the page instance
			// to when onClick is called
			Class<?>[] paramClasses = new Class<?>[params.length];

			for (int i = 0; i < params.length; ++i) {
				paramClasses[i] = params[i].getClass();
			}
			
			Constructor<? extends FrontendPage> constructor = redirectPage.getConstructor(paramClasses);
			
			page = (FrontendPage)constructor.newInstance(params);
		} 
		catch (Exception ex) 
		{
			logger.error("Could not redirect to page", ex);
			throw new AssertionError(
				"Could not instantiate page class: " + redirectPage
			);
		}
		
		setResponsePage(page);
	}
}
