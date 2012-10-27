package notification 

import javax.mail._
import javax.mail.internet._
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import java.util.Properties
import play.Logger



object SendMail {
	val SMTP_HOST_NAME = "smtp.sendgrid.net";
    val IDEASBA_MAIL  = "ideasba@gmail.com";
 

    def send(subject: String, contentHTML: String,
                contentTEXT: String, mail: String) = {

        val props: Properties = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", new java.lang.Integer(587));
        props.put("mail.smtp.auth", "true");
 
		val auth = new SMTPAuthenticator();
        val mailSession = Session.getInstance(props, auth);

        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        val transport = mailSession.getTransport();
 
        val message = new MimeMessage(mailSession);
 
        val multipart = new MimeMultipart("alternative");
 
        val part1 = new MimeBodyPart();
        part1.setText(contentTEXT);
 
        val part2 = new MimeBodyPart();
        part2.setContent(contentHTML, "text/html");
 
        multipart.addBodyPart(part1);
        multipart.addBodyPart(part2);
 
        message.setContent(multipart);
        message.setFrom(new InternetAddress(IDEASBA_MAIL));
        message.setSubject(subject);
        message.addRecipient(Message.RecipientType.TO,
             new InternetAddress(mail));
 
        transport.connect();
        transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
        transport.close();
        Logger.info("SendMail sent an email to: " + mail)
    }

	class SMTPAuthenticator extends javax.mail.Authenticator {
        override def getPasswordAuthentication(): PasswordAuthentication = {
        	val username = "ideasba";
    		val password  = "secret";
        return new PasswordAuthentication(username, password);
        }
    }

 
}

