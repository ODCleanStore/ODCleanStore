package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.lang.reflect.Constructor;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class RedirectWithParamButton<T> extends Link<T>
{
	private static final long serialVersionUID = 1L;
	
	private Class<? extends FrontendPage> redirectPage;
	private Long param;
	
	public RedirectWithParamButton(final Class<? extends FrontendPage> redirectPage, 
		final Long param, final String compName) 
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
				redirectPage.getConstructor(new Class[]{Long.class});
			
			page = (FrontendPage) constructor.newInstance(param);
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
