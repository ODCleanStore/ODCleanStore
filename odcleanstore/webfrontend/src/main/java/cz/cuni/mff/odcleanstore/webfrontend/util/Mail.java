package cz.cuni.mff.odcleanstore.webfrontend.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*; 
import java.io.*;

/**
 * A helper class to send emails.
 * 
 * @author Dusan Rychnovsky
 *
 */
public class Mail 
{
	private String from;
	private String to;
	private String subject;
	private String messageBody;
	private ArrayList<String> attachements;
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param messageBody
	 */
	public Mail(String from, String to, String subject, String messageBody) 
	{
		this.attachements = new ArrayList();
		
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.messageBody = messageBody;
	}
	
	/**
	 * 
	 * @param attachement
	 */
	public void addAttachement(String attachement)
	{
		attachements.add(attachement);
	}
	
	/**
	 * Sends the represented message through the given mail-server.
	 * 
	 * This code snippet originates from a StackOverflow thread 
	 * (http://stackoverflow.com/questions/2423760/how-can-i-send-email-from-a-wicket-application)
	 * 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	private void send(String mailServer) throws AddressException, MessagingException
	{
		// Setup mail server 
        Properties props = System.getProperties(); 
        props.put("mail.smtp.host", mailServer); 

        // Get a mail session 
        Session session = Session.getDefaultInstance(props, null); 

        // Define a new mail message 
        Message message = new MimeMessage(session); 
        message.setFrom(new InternetAddress(from)); 
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); 
        message.setSubject(subject); 

        // Create a message part to represent the body text 
        BodyPart messageBodyPart = new MimeBodyPart(); 
        messageBodyPart.setText(messageBody); 

        //use a MimeMultipart as we need to handle the file attachments 
        Multipart multipart = new MimeMultipart(); 

        //add the message body to the mime message 
        multipart.addBodyPart(messageBodyPart); 

        // add any file attachments to the message 
        addAtachments(attachements, multipart); 

        // Put all message parts in the message 
        message.setContent(multipart); 

        // Send the message 
        Transport.send(message); 
	}
	
	private void addAtachments(ArrayList<String> attachments, Multipart multipart) 
		throws MessagingException, AddressException 
	{ 
		for (String filename : attachements) 
		{  
		    MimeBodyPart attachmentBodyPart = new MimeBodyPart(); 
		
		    // use a JAF FileDataSource as it does MIME type detection 
		    DataSource source = new FileDataSource(filename); 
		    attachmentBodyPart.setDataHandler(new DataHandler(source)); 
		
		    //assume that the filename you want to send is the same as the 
		    //actual file name - could alter this to remove the file path 
		    attachmentBodyPart.setFileName(filename); 
		
		    //add the attachment 
		    multipart.addBodyPart(attachmentBodyPart); 
		} 
	}	
}
