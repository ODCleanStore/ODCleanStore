package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.lang.reflect.Constructor;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * 
 * @author Jan Michelfeit
 *
 */
public class AssignedGroupRedirectButton extends Link<String>
{
	private static final long serialVersionUID = 1L;
	
	private Class<? extends FrontendPage> redirectPage;
	private Integer groupId;
	private Integer transformerInstanceId;
	
	public AssignedGroupRedirectButton(final Class<? extends FrontendPage> redirectPage, 
		final Integer groupId, final Integer transformerInstanceId, final String compName) 
	{
		super(compName);

		this.redirectPage = redirectPage;
		this.groupId = groupId;
		this.transformerInstanceId = transformerInstanceId;
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
				redirectPage.getConstructor(new Class[]{Integer.class, Integer.class});
			
			page = (FrontendPage) constructor.newInstance(groupId, transformerInstanceId);
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
