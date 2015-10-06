   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import static com.edhkle.joopz.MaildirMessageStore.log;
import java.io.IOException;
import java.util.logging.Level;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author ehansen
 */
public class MboxMessageStore implements JavamailMessageStore {
    Session session;
    Folder  inbox;
    Store   store;
    
    @Override
    public void testReadMessages(int count) throws IOException, MessagingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Message[] getAllNewMessages() throws JoopzMessageServiceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Session getSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Store getStore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Folder getInbox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
    @Override
    public Message[] getAllNewMessagesDebug() {
        Message[] msgs = new Message[3];
        try {
            MimeMessage m = new MimeMessage(session);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress("16037597215.911b@joopzy"));
            m.setFrom("19787713151@vtext.com");
            m.setSubject("SMS Subject");
            Multipart mp = new MimeMultipart("alternative");
            MimeBodyPart bp = new MimeBodyPart();
            bp.setText("SMS Text Content\nSent by joopz.com", "utf-8");
            mp.addBodyPart(bp);
            m.setContent(mp);
            msgs[0] = m;
            m = new MimeMessage(session);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress("19735257291.27e3@joopzy"));
            m.setFrom("16039434333@tmomail.com");
            m.setSubject("SMS Subject");
            mp = new MimeMultipart("alternative");
            bp = new MimeBodyPart();
            bp.setText("SMS Text Content\nSent by joopz.com");
            mp.addBodyPart(bp);
            m.setContent(mp);
            msgs[1] = m;
            m = new MimeMessage(session);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress("19782707839.ebb3@joopzy"));
            m.setFrom("16039434333@tmomail.com");
            m.setSubject("SMS Subject");
            mp = new MimeMultipart("alternative");
            bp = new MimeBodyPart();
            bp.setText("SMS Text Content\nSent by joopz.com");
            mp.addBodyPart(bp);
            m.setContent(mp);
            msgs[2] = m;
        } catch (MessagingException e) {
            log.log(Level.SEVERE, "Caught Messaging Exception building debug messages!", e);
        }
        return msgs;
    }  
}
