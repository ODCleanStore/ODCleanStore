package cz.cuni.mff.odcleanstore.webfrontend.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * A helper class to send emails.
 * 
 * @author Dusan Rychnovsky
 *
 */
public class Mail 
{
	private static Logger logger = Logger.getLogger(Mail.class);
	
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
		this.attachements = new ArrayList<String>();
		
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.messageBody = messageBody;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRecipient()
	{
		return to;
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
	 * Sends the represented message through the given custom mail-server.
	 * 
	 * This code snippet originates from a StackOverflow thread 
	 * (http://stackoverflow.com/questions/2423760/how-can-i-send-email-from-a-wicket-application)
	 * 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public void send(String mailServer) throws AddressException, MessagingException
	{
		// Setup mail server
        Properties props = System.getProperties(); 
        props.put("mail.smtp.host", mailServer); 

        // Get a mail session 
        Session session = Session.getDefaultInstance(props, null); 

		// Create the message to be send
		Message message = createMessage(session);
		
        // Send the message 
        Transport.send(message); 
	}
	
	/**
	 * Sends the represented message through Gmail SMTP server using the given
	 * Gmail account credentials.
	 * 
	 * This code snippet originates from a blog post
	 * (http://technology-for-human.blogspot.com/2011/08/sending-emails-using-java-mail-and.html)
	 * 
	 * @param gmailAddress
	 * @param gmailPassword
	 * @throws MessagingException
	 */
	public void sendThroughGmail(String gmailAddress, String gmailPassword) 
		throws MessagingException
	{
		logger.debug("Sending email through GMail account: " + gmailAddress);
		logger.debug("Mail recipient: " + to);
		
		// Setup mail server
		Properties props = new Properties();
		
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", "smtp.gmail.com");
		props.put("mail.smtps.auth", "true");

        // Get a mail session 
		Session session = Session.getInstance(props);
		session.setDebug(false);

		// Create the message to be send
		Message message = createMessage(session);
		
		// Send the message
		Transport transportSSL = session.getTransport();
		transportSSL.connect("smtp.gmail.com", 465, gmailAddress, gmailPassword);
		transportSSL.sendMessage(message, message.getAllRecipients());
		transportSSL.close();
	}
	
	/**
	 * Creates a {@link javax.mail.Message} object encapsulating the represented
	 * message.
	 * 
	 * @param session
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private Message createMessage(Session session) 
		throws AddressException, MessagingException
	{
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
        
        return message;
	}
	
	/**
	 * Processes the given array of file-paths and adds them as message
	 * attachements.
	 * 
	 * @param attachments
	 * @param multipart
	 * @throws MessagingException
	 * @throws AddressException
	 */
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
