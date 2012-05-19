package cz.cuni.mff.odcleanstore.webfrontend.util;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

/**
 * A helper class to send emails confirming new account registrations.
 * 
 * @author Dusan Rychnovsky
 *
 */
public class NewAccountMail extends Mail 
{
	private static final String MESSAGE_BODY_TEMPLATE =
		"A new user account has been created at odcleanstore.cz using your email address. " +
		"Corresponding credentials are (username/password): @USERNAME@/@PASSWORD@.";

	/**
	 * 
	 * @param user
	 * @param password
	 */
	public NewAccountMail(User user, String password)
	{
		super(
			"no-reply@odcleanstore.cz",
			user.getEmail(),
			"ODCleanStore - new user account confirmation",
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
