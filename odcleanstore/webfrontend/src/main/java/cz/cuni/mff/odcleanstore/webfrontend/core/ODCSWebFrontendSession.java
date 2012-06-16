package cz.cuni.mff.odcleanstore.webfrontend.core;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

public class ODCSWebFrontendSession extends AuthenticatedWebSession
{
	private static final long serialVersionUID = 1L;
	
	private User user;
	
	public ODCSWebFrontendSession(Request request) 
	{
		super(request);
		
		user = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ODCSWebFrontendSession get()
	{
		return (ODCSWebFrontendSession) Session.get();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAuthenticated()
	{
		return user != null;
	}
	
	/**
	 * 
	 * @param user
	 */
	public void setUser(User user) 
	{
		this.user = user;		
	}
	
	/**
	 * 
	 * @return
	 */
	public User getUser()
	{
		return user;
	}
	
	@Override
	public Roles getRoles() 
	{
		if (user == null)
			return new Roles(new String[0]);
		
		return new Roles(user.getRoleLabels());
	}
	
	@Override
	public void invalidate()
	{
		user = null;
		
		super.invalidate();
	}
}
