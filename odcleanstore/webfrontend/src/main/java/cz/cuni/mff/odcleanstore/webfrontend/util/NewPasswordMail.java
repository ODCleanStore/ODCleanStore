package cz.cuni.mff.odcleanstore.webfrontend.util;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

/**
 * A helper class to send emails confirming new account registrations.
 * 
 * @author Dusan Rychnovsky
 *
 */
public class NewPasswordMail extends Mail 
{
	private static final String MESSAGE_BODY_TEMPLATE =
		"A new password has been generated at odcleanstore.cz " +
		"for a user account associated with your email address. " +
		"Corresponding credentials are (username/password): @USERNAME@/@PASSWORD@.";

	/**
	 * 
	 * @param user
	 * @param password
	 */
	public NewPasswordMail(User user, String password)
	{
		super(
			"no-reply@odcleanstore.cz",
			user.getEmail(),
			"ODCleanStore - password changed confirmation",
			substituteBodyTemplate(MESSAGE_BODY_TEMPLATE, user.getUsername(), password)
		);
	}
	
	/**
	 * Returns the given message-body-template substituted by the given variables.
	 *  
	 * @param template
	 * @param username
	 * @param password
	 * @return
	 */
	static String substituteBodyTemplate(String template, String username, String password)
	{
		return 
			template
				.replace("@USERNAME@", username)
				.replace("@PASSWORD@", password);
	}
}
