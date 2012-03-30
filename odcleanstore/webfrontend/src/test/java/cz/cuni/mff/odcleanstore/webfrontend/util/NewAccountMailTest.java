package cz.cuni.mff.odcleanstore.webfrontend.util;

import static org.junit.Assert.*;

import javax.mail.MessagingException;

import org.junit.Test;
import org.mockito.Mockito;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

public class NewAccountMailTest 
{
	@Test
	public void testMessageBodySubstitution() 
	{
		String messageTemplate =
			"A new user account has been created at odcleanstore.cz using your email address. " +
			"Corresponding credentials are (username/password): @USERNAME@/@PASSWORD@.";
		
		String substitutedMessage =
			"A new user account has been created at odcleanstore.cz using your email address. " +
			"Corresponding credentials are (username/password): dusanr/topsecret13.";
		
		String username = "dusanr";
		String password = "topsecret13";
		
		assertEquals(
			substitutedMessage,
			NewAccountMail.substituteBodyTemplate(messageTemplate, username, password)
		);
	}
	
	/*
	@Test
	public void testMailSending() throws MessagingException
	{
		User user = Mockito.mock(User.class);
		
		Mockito.when(user.getUsername()).thenReturn("dusanr");
		Mockito.when(user.getEmail()).thenReturn("dusan.rychnovsky@gmail.com");
		
		Mail message = new NewAccountMail(user, "abrakadabra");
		message.sendThroughGmail("odcleanstore@gmail.com", "odcleanstore2012");
	}
	 */
}
