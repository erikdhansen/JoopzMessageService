/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ehansen
 */
public class JoopzEmailService {
    final static Logger log = Logger.getLogger(JoopzEmailService.class.getName());
    
    public JoopzEmailService() {
        
    }
    
    public void sendEmail(JoopzOutgoingMessage msg) throws MessagingException, SQLException, IOException {
        boolean result = true;
        String to = getToAddress(msg);
        String from = getFromAddress(msg);
        String host = "localhost";
        
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        
        Session session = Session.getDefaultInstance(props);
        
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("SMS Message");
        message.setText(msg.getMessage().concat("\n[Sent via: joopz.com]"));
        Transport.send(message);
        log.info("Sent Email: To: " + to + " From: " + from + " Msg: " + msg.getMessage());
    }
    
    private String getFromAddress(JoopzOutgoingMessage msg) throws SQLException {
        String fromPhone = DBUtils.getUserPhoneNumber(msg.getUserId());
        String uniqueId  = DBUtils.getContactUniqueId(msg.getContactId());
        String partnerDomain = DBUtils.getPartnerDomainForUser(msg.getUserId());
        return fromPhone + "." + uniqueId + "@" + partnerDomain;
    }
    
    private String getToAddress(JoopzOutgoingMessage msg) throws SQLException, IOException {
        String toPhone = DBUtils.getContactPhoneNumber(msg.getContactId());
        if(toPhone.startsWith("1")) {
            toPhone = toPhone.substring(1);
        }
        String smtpGateway = DBUtils.getCarrierSmtpGateway(toPhone);
        return toPhone + "@" + smtpGateway;
    }
}
