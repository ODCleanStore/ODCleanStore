package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.lang.reflect.Constructor;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * A generic button to redirect to another page.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RedirectButton extends Link<String>
{
	private static final long serialVersionUID = 1L;
	
	/** the class of the page to redirect to */
	private Class<? extends FrontendPage> redirectPage;
	
	/**
	 * 
	 * @param compName
	 * @param redirectPage the class of the page to redirect to
	 */
	public RedirectButton(final String compName, final Class<? extends FrontendPage> redirectPage) 
	{
		super(compName);

		this.redirectPage = redirectPage;
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
			Constructor<? extends FrontendPage> constructor = redirectPage.getConstructor();
			
			page = (FrontendPage) constructor.newInstance();
		} 
		catch (Exception ex) 
		{
			throw new AssertionError(
				"Could not instantiate page class: " + redirectPage
			);
		}
		
		setResponsePage(page);
	}
}
