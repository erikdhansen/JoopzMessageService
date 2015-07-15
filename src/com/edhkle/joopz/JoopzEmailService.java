/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.util.logging.Logger;

/**
 *
 * @author ehansen
 */
class JoopzEmailService {
    final static Logger log = Logger.getLogger(JoopzEmailService.class.getName());
    
    public static boolean sendEmail(JoopzOutgoingMessage message) {
        boolean result = true;
        log.info("Sending email for JoopzOutgoingMessage: ".concat(message.toString()));
        return result;
    }
}
