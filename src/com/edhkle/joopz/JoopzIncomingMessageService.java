/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;

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
                    msgs = msgStore.getAllNewMessages();
                    List<JoopzIncomingMessage> incomingMsgs = new LinkedList<>();
                    for(Message m : msgs) {
                        incomingMsgs.add(new JoopzIncomingMessage(m));
                    }
                    log.info("Processed " + msgs.length + " emails into " + incomingMsgs.size() + " Joopz Incoming Messages");
                    int good = 0;
                    int bad = 0;
                    for(JoopzIncomingMessage im : incomingMsgs) {
                        try {
                            processJoopzIncomingMessage(im);
                            good++;
                        } catch (JoopzMessageServiceException e) {
                            log.log(Level.WARNING, "Caught JoopzMessageServiceException processing incoming message", e);
                            log.warning("Problem Incoming Message:\n" + im.toString());
                            bad++;
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
