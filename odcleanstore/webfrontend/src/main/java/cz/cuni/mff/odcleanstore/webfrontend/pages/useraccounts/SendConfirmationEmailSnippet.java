package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.configuration.WebFrontendConfig;
import cz.cuni.mff.odcleanstore.webfrontend.util.Mail;

/**
 * A code-snippet to send confirmation emails.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class SendConfirmationEmailSnippet extends CodeSnippet
{
	private static Logger logger = Logger.getLogger(SendConfirmationEmailSnippet.class);
	
	private WebFrontendConfig config;
	private Mail mail;
	
	/**
	 * 
	 * @param user
	 * @param password
	 */
	public SendConfirmationEmailSnippet(WebFrontendConfig config, Mail mail)
	{
		this.config = config;
		this.mail = mail;
	}
	
	@Override
	public void execute() throws MessagingException 
	{
		try {
			mail.sendThroughGmail(config.getGmailAddress(), config.getGmailPassword());
		}
		catch (MessagingException ex) 
		{
			logger.error(ex.getMessage());
			
			throw new MessagingException(
				"Could not send confirmation email to: " + mail.getRecipient()
			);
		}
	}
}
