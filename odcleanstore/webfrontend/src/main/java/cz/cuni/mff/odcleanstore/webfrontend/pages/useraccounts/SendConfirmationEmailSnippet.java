package cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts;

import javax.mail.MessagingException;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.configuration.WebFrontendConfig;
import cz.cuni.mff.odcleanstore.webfrontend.util.Mail;

public class SendConfirmationEmailSnippet extends CodeSnippet
{
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
			// TODO: log error
			
			throw new MessagingException(
				"Could not send confirmation email to: " + mail.getRecipient()
			);
		}
	}
}
