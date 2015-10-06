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
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
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
    final static String url  = "maildir:/home/ehansen/Maildir";
    
    public MaildirMessageStore() throws JoopzMessageServiceException {
        session = Session.getInstance(properties);
        try {
            store = session.getStore(new URLName(url));
            store.connect();
            inbox = store.getFolder("inbox");
            //inbox = store.getDefaultFolder();
            log.info("Retrieved default folder from mail store: " + inbox.getFullName());
            //log.info("Default folder contains folder:" + inbox.list()[0].getName());
            //inbox = inbox.getFolder(inbox.list()[0].getName());
            // Test that mail store is readable -- that's sufficient for initialization
            inbox.open(Folder.READ_WRITE);
        } catch (Exception e) {
            Logger.getLogger(MaildirMessageStore.class.getName()).log(Level.SEVERE, null, e);
            throw new JoopzMessageServiceException("Unable to initialize Maildir message store: URL=" + url, e);
        }
    }
    
    @Override
    public Message[] getAllNewMessages() throws JoopzMessageServiceException {
        if(!inbox.isOpen()) {
            try {
                inbox.open(Folder.READ_WRITE);
            } catch (MessagingException e) {
                throw new JoopzMessageServiceException("Unable to open INBOX!  Caught MessagingException!", e);
            }
        }
        try {
            log.info(inbox.getFullName() + " open for read-write access (UNREAD msg count = " + inbox.getUnreadMessageCount() + ")");
        } catch (MessagingException ex) {
            Logger.getLogger(MaildirMessageStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        Message[] msgs;
        try {
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseen = new FlagTerm(seen, false);
            msgs = inbox.search(unseen);
            log.info("Retrieved " + msgs.length + " email messages for processing");
        } catch (MessagingException e) {
            throw new JoopzMessageServiceException("Unable to retrieve messages from mail store! (" + inbox.getName() + ")", e);
        }
        return msgs;
    }
    
    @Override
    public void testReadMessages(int count) throws IOException, MessagingException {
        if(!inbox.isOpen()) {
            inbox.open(Folder.READ_WRITE);
        }
        log.info(inbox.getFullName() + " open as read-write (msg count = " + inbox.getMessageCount() + ")");
        Message[] messages = inbox.getMessages();
        log.info("Retrieved " + messages.length + " messages from INBOX");
        for(Message m : messages) {
           System.out.println("MSG Subject: " + m.getSubject());
           m.writeTo(System.out);            
        }
        log.info("Done reading messages");
        log.info("Incoming message service test complete.");
    }
    
    @Override
    public Session getSession() {
        return session;
    }
    
    @Override
    public Store getStore() {
        return store;
    }
    
    @Override
    public Folder getInbox() {
        return inbox;
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
