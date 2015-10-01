/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Multipart;

/**
 *
 * @author ehansen
 */
public class JoopzIncomingMessage {
    final static Logger log = Logger.getLogger(JoopzIncomingMessage.class.getName());
    
    Message m;
    String sourcePhone;
    String uniqueId;
    boolean groupDestination = false;
    List<String> destPhones = new LinkedList<>();
    String subject;
    String body;
    
    public JoopzIncomingMessage(Message m) throws JoopzMessageServiceException {
        try {
            // Save the email message itself...probably not necessary...maybe
            // just the messages ID?
            this.m = m;
            
            // Read From/To/Subject
            String from = m.getFrom()[0].toString();
            sourcePhone = from.substring(0, from.indexOf("@"));
            String to = m.getAllRecipients()[0].toString();
            destPhones.add(to.substring(0, to.indexOf(".")));
            uniqueId = from.substring(to.indexOf(".") + 1);
            subject = m.getSubject();
            
            // Read message body
            Multipart mp = (Multipart) m.getContent();
            body = (String)mp.getBodyPart(0).getContent();
        } catch (Exception e) {
            throw new JoopzMessageServiceException("Could not parse email into JoopzIncomingMessage!", e);     
        }
    }
    
    public Message getMessage() {
        return m;
    }

    public void setMessage(Message m) {
        this.m = m;
    }

    public String getSourcePhone() {
        return sourcePhone;
    }

    public void setSourcePhone(String sourcePhone) {
        this.sourcePhone = sourcePhone;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isGroupDestination() {
        return groupDestination;
    }

    public void setGroupDestination(boolean groupDestination) {
        this.groupDestination = groupDestination;
    }

    public List<String> getDestPhones() {
        return destPhones;
    }

    public void setDestPhones(List<String> destPhones) {
        this.destPhones = destPhones;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    
}
