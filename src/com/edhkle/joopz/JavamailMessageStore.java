/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 *
 * @author ehansen
 */
public interface JavamailMessageStore {
    
    public static enum TYPE {
        MBOX,
        MAILDIR
    }
    
    public void testReadMessages(int count) throws IOException, MessagingException;
    public Message[] getAllNewMessages() throws JoopzMessageServiceException;
}
