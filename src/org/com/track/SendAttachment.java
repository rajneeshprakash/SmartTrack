package org.com.track;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendAttachment
{
 //public Store store;
 private static  String SMTP_HOST_NAME="smtp.gmail.com";
  private static String SMTP_AUTH_USER="smarttrack.ise.ait@gmail.com";
  private static String SMTP_AUTH_PWD="SmartTrack@2014";
  private static String SMTP_PORT="465";
  private static String EMAIL_ID_TO;
  private static String SSL_FACTORY="javax.net.ssl.SSLSocketFactory";
  //String from="checkstudent08@gmail.com";
  String filename=null;
  //Properties mailinfo;
  String messageinfo;

 public SendAttachment(String email,String messageinfo,List<String> filePaths)
 {
	 EMAIL_ID_TO=email;
	 this.messageinfo=messageinfo;
     //Set the host smtp address
     Properties props = new Properties();
     props.put("mail.smtp.starttls.enable","true");
     props.put("mail.smtp.host", SMTP_HOST_NAME);
     props.put("mail.smtp.auth", "true");
    // props.put("mail.debug", "false");
     props.put("mail.smtp.port", SMTP_PORT);
     props.put("mail.smtp.socketFactory.port", SMTP_PORT);
     props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
     props.put("mail.smtp.socketFactory.fallback", "false");

   Authenticator auth=new SMTPAuthenticator(SMTP_AUTH_USER,SMTP_AUTH_PWD);
   try
   {
    Session session=Session.getInstance(props,auth);
    MimeMessage msg=new MimeMessage(session);
    msg.setFrom(new InternetAddress(SMTP_AUTH_USER));
    InternetAddress[] address={new InternetAddress(EMAIL_ID_TO)};
    msg.setRecipients(Message.RecipientType.TO,address);
    msg.setSubject("Some attachment through Android code");
    msg.setSentDate(new Date());
    MimeBodyPart bp1=new MimeBodyPart();
    bp1.setText(messageinfo);
    Multipart mp=new MimeMultipart();
    mp.addBodyPart(bp1);
    //file attachment
    for (String file : filePaths)
    {
    	MimeBodyPart bp2=new MimeBodyPart();
        DataSource atc=new FileDataSource(file);
        bp2.setDataHandler(new DataHandler(atc));
        bp2.setFileName(file);
        mp.addBodyPart(bp2);
        
    }
    msg.setContent(mp);

    Transport.send(msg);
    
    System.out.println("Message Is sent Now..Thanx");

    }catch(MessagingException mex) 
	{
	  mex.printStackTrace();
	  Exception ex=null;
	  if((ex=mex.getNextException())!=null)
	  {
	    ex.printStackTrace();
	  }// end of if
    }
  }

}
