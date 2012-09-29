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
	
	public static boolean isAuthorizedForEntityEditing(AuthoredEntity entity)
	{
		return isAuthorizedForEntityEditing(entity.getAuthorId());
	}
	
	public static boolean isAuthorizedForEntityEditing(Integer authorId)
	{
		User user = ODCSWebFrontendSession.get().getUser();
		return user.hasAssignedRole(SUPER_ROLE) || user.getId().equals(authorId);
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
