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
public class JoopzMessageService {
    final static Logger log = Logger.getLogger(JoopzMessageService.class.getName());
    
    JoopzOutgoingMessageService outgoing;
    JoopzIncomingMessageService incoming;
    
    public JoopzMessageService() throws JoopzMessageServiceException {
        outgoing = new JoopzOutgoingMessageService();
        log.info("Created outgoing message service: ".concat(outgoing.toString()));
        incoming = new JoopzIncomingMessageService();
        log.info("Created incoming message service: ".concat(incoming.toString()));
    }
    
    public void startJoopzServices() {
        log.info("Starting up incoming/outgoing services...");
        (new Thread(incoming)).start();
        (new Thread(outgoing)).start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        JoopzMessageService mainService = new JoopzMessageService();
        mainService.startJoopzServices();

    }
    
}
