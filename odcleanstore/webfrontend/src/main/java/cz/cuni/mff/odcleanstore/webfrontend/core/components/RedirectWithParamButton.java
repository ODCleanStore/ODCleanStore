package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
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
	
	/** the constructor argument of the page */
	private Integer param;
	
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
		this.param = param;
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
			Constructor<? extends FrontendPage> constructor = 
				redirectPage.getConstructor(new Class[]{Integer.class});
			
			page = (FrontendPage) constructor.newInstance(param);
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
