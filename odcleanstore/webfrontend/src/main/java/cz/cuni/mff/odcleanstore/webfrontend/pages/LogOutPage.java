package cz.cuni.mff.odcleanstore.webfrontend.pages;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LogOutPage extends WebPage
{
	public static final String REDIRECT_PAGE_PARAM_KEY = "redirectpage";
	
	private static final long serialVersionUID = 1L;
	
	public LogOutPage(final PageParameters params)
	{
		String pageClassName = params.get(REDIRECT_PAGE_PARAM_KEY).toString();
		
		Class<? extends Page> pageClass = getResponsePageClass(pageClassName);
		
		getSession().invalidate();
		
		getSession().info("User successfuly logged out.");
		setResponsePage(pageClass);
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends Page> getResponsePageClass(String pageClassName)
	{
		if (pageClassName == null)
			return getApplication().getHomePage();
		
		try {
			return (Class<? extends Page>) Class.forName(pageClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

}
