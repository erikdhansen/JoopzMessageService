/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            try {
                if(msgStore != null) {
                    try {
                        msgStore.testReadMessages(10);
                    } catch (IOException ex) {
                        Logger.getLogger(JoopzIncomingMessageService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MessagingException ex) {
                        Logger.getLogger(JoopzIncomingMessageService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                log.info("Sleeping for 10 seconds...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
