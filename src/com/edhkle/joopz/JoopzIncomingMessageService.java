/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 *
 * @author ehansen
 */
public class JoopzIncomingMessageService implements Runnable {
    final static Logger log = Logger.getLogger(JoopzIncomingMessageService.class.getName());
    JavamailMessageStore msgStore = null;
    
    public JoopzIncomingMessageService(JavamailMessageStore.TYPE type) throws JoopzMessageServiceException {
        switch(type) {
            case MBOX:
                msgStore = new MboxMessageStore();
                break;
            case MAILDIR:
                msgStore = new MaildirMessageStore();
                break;
            default:
                throw new JoopzMessageServiceException("Unknown mail store type: " + type);
        }
        log.info("Initialized JoopzIncomingMailService message store TYPE=" + type + " Class=" + msgStore.getClass().getSimpleName());
    }

    @Override
    public void run() {
        while(true) {
            if(msgStore == null) {
                log.warning("msgStore is null!  Cannot retrieve new incoming messages!");
            } else {
                Message[] msgs;
                try {
                    msgs = msgStore.getAllNewMessagesDebug();
                    List<JoopzIncomingMessage> incomingMsgs = new LinkedList<>();
                    int good = 0;
                    int bad  = 0;
                    for(Message m : msgs) {
                        try {
                            JoopzIncomingMessage im = new JoopzIncomingMessage(m);
                            incomingMsgs.add(new JoopzIncomingMessage(m));
                            good++;
                        } catch (JoopzMessageServiceException e) {
                            log.log(Level.WARNING, "Failed to parse Mail Message into JoopzIncomingMessage", e);
                            log.warning("Marking message[" + m.getMessageNumber() + "] as SEEN");
                            bad++;
                            try {
                                m.setFlag(Flag.SEEN, true);
                                m.setFlag(Flag.DELETED, true);
                                m.saveChanges();
                            } catch (MessagingException ex) {
                                Logger.getLogger(JoopzIncomingMessageService.class.getName()).log(Level.SEVERE, null, ex);
                                log.severe("Failed to set SEEN flag on message[" + m.getMessageNumber());
                            }
                        }
                    }
                    log.info("Done processing incoming emails.  Results: Total=" + msgs.length + " Good=" + good + " Bad=" + bad);
                    for(JoopzIncomingMessage im : incomingMsgs) {
                        try {
                            processJoopzIncomingMessage(im);
                        } catch (JoopzMessageServiceException e) {
                            log.log(Level.WARNING, "Caught JoopzMessageServiceException processing incoming message", e);
                      }
                    }
                    log.info("Incoming Message Processing Cycle complete.  Processed " + incomingMsgs.size() + " incoming messages.  Good=" + good + "  Bad=" + bad);                    
                } catch (JoopzMessageServiceException ex) {
                    Logger.getLogger(JoopzIncomingMessageService.class.getName()).log(Level.SEVERE, null, ex);
                }
                log.info("Sleeping for 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.log(Level.WARNING, "InterruptedException", e);
                }
            }
        }
    }
    
    private void processJoopzIncomingMessage(JoopzIncomingMessage im) throws JoopzMessageServiceException {
        log.info("*** processIncomingJoopzMessage: " + im.toString());
        try {
            Map<String,String> contact = DBUtils.getContactFromUniqueId(im.getUniqueId());
            log.info("Retrieved Contact Info: " + contact.toString());
        } catch (SQLException e) {
            throw new JoopzMessageServiceException("Failed to convert uniqueId=" + im.getUniqueId() + " into a contact!", e);
        }
    }
    
    public void test() {
        log.info("Running test on incoming message service");
        if(msgStore == null) {
            log.warning("msgStore is null!  Cannot run test on email store!");
        } else {
            try {
                msgStore.testReadMessages(10);
                log.info("msgStore test is complete.");
            } catch (Exception e) {
                log.log(Level.WARNING, "Exception running incoming message service test!", e);
            }
        }
    }
}
