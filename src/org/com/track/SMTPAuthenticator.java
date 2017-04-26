package org.com.track;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class SMTPAuthenticator extends Authenticator {
    String username="checkstudent11@gmail.com";
    String password="studentcheck";
    
    public SMTPAuthenticator(String u, String p) {
        this.username=u;
        this.password=p;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}        