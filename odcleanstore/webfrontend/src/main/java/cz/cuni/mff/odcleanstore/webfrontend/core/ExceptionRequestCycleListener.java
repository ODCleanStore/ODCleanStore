package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.log4j.Logger;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

public class ExceptionRequestCycleListener extends AbstractRequestCycleListener
{
	private static Logger logger = Logger.getLogger(ExceptionRequestCycleListener.class);
	
	public IRequestHandler onException(RequestCycle cycle, Exception ex)
	{
		// TODO: direct handling of session expired in Wicket would be better
		if (ex instanceof WicketRuntimeException
			&& ex.getCause() instanceof NoSuchMethodException
			&& ex.getMessage() != null
			&& ex.getMessage().contains("Class does not have a visible default contructor")
			&& !ODCSWebFrontendSession.get().isAuthenticated()
			&& ex.getCause().getMessage().endsWith("Page.<init>()"))
		{
			ODCSWebFrontendSession.get().error("Your session has expired.");
			cycle.setResponsePage(ODCSWebFrontendApplication.get().getHomePage());
		}
		logger.error(ex);
		return cycle.getRequestHandlerScheduledAfterCurrent();
	}
}