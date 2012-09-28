package cz.cuni.mff.odcleanstore.webfrontend.core;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

/**
 * Helper class for authorization utility methods
 * @author Jan Michelfeit
 * 
 */
public final class AuthorizationHelper
{
	private final static String SUPER_ROLE = Role.ADM;
	
	public static boolean isAuthorizedForEntityEditing(AuthoredEntity entity, String requiredRole)
	{
		return isAuthorizedForEntityEditing(entity.getAuthorId(), requiredRole);
	}
	
	public static boolean isAuthorizedForEntityEditing(Integer authorId, String requiredRole)
	{
		User user = ODCSWebFrontendSession.get().getUser();
		if (user.hasAssignedRole(SUPER_ROLE))
			return true;
		else if (user.hasAssignedRole(requiredRole) && user.getId().equals(authorId))
			return true;
		else
			return false;
	}
	
	public static boolean isAuthorizedForSettingDefaultPipeline() 
	{
		User user = ODCSWebFrontendSession.get().getUser();
		return user.hasAssignedRole(Role.ADM);
	}

	/** Disable constructor for a utility class. */
	private AuthorizationHelper()
	{ }
}
