package cz.cuni.mff.odcleanstore.webfrontend;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

public class WicketSession extends AuthenticatedWebSession
{
	private User user;
	private String[] roles;
	
	/**
	 * 
	 * @param request
	 */
	public WicketSession(Request request) 
	{
		super (request);
		
		user = null;
		roles = new String[] { };
	}

	public static WicketSession get()
	{
		return (WicketSession) Session.get();
	}
	
	@Override
	public Roles getRoles() 
	{
		return new Roles(roles);
	}
	
	public User getUser() 
	{
		return user;
	}

	public void setUser(User user) 
	{
		this.user = user;
		
		List<String> roleNames = new LinkedList<String>();
		
		for (Role role : user.getRoles())
			roleNames.add(role.getName());
		
		this.roles = roleNames.toArray(new String[roleNames.size()]);
	}
}
