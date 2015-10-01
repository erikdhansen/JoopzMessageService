/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 *
 * @author ehansen
 */
public class MaildirMessageStore implements JavamailMessageStore {
    final static Logger log = Logger.getLogger(MaildirMessageStore.class.getName());
    Session session = null;
    Store   store = null;
    Folder  inbox = null;
    
    Properties properties = new Properties();
    final static String user = "joopz";
    final static String url  = "maildir:/home/joopz/Maildir/";
    
    public MaildirMessageStore() throws JoopzMessageServiceException {
        session = Session.getInstance(properties);
        try {
            store = session.getStore(new URLName(url));
            store.connect();
            
            // Test that mail store is readable -- that's sufficient for initialization
            inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_WRITE);
        } catch (Exception e) {
            Logger.getLogger(MaildirMessageStore.class.getName()).log(Level.SEVERE, null, e);
            throw new JoopzMessageServiceException("Unable to initialize Maildir message store: URL=" + url, e);
        }
    }
    
    @Override
    public void testReadMessages(int count) throws IOException, MessagingException {
        if(!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }
        log.info("INBOX opened successfully as read-only (msg count = " + inbox.getMessageCount() + ")");
        Message[] messages = inbox.getMessages();
        log.info("Retrieved " + messages.length + " messages from INBOX");
        for(Message m : messages) {
           System.out.println("MSG Subject: " + m.getSubject());
           m.writeTo(System.out);            
        }
        log.info("Done reading messages");
        log.info("Incoming message service test complete.");
    }
}
