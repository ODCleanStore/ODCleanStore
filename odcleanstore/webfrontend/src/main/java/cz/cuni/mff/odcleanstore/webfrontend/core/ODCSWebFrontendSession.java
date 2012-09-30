package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

public class ODCSWebFrontendSession extends AuthenticatedWebSession
{
	private static final long serialVersionUID = 1L;
	
	private User user;
	private final Map<Integer, Integer> qaPipelineRulesNavigationMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> dnPipelineRulesNavigationMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> oiPipelineRulesNavigationMap = new HashMap<Integer, Integer>();
	
	public ODCSWebFrontendSession(Request request) 
	{
		super(request);
		
		this.user = null;
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
	
	public Map<Integer, Integer> getQaPipelineRulesNavigationMap()
	{
		return qaPipelineRulesNavigationMap;
	}

	public Map<Integer, Integer> getDnPipelineRulesNavigationMap()
	{
		return dnPipelineRulesNavigationMap;
	}

	public Map<Integer, Integer> getOiPipelineRulesNavigationMap()
	{
		return oiPipelineRulesNavigationMap;
	}

	@Override
	public void invalidate()
	{
		user = null;
		
		super.invalidate();
	}
}
