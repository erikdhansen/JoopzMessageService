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
        incoming = new JoopzIncomingMessageService(JavamailMessageStore.TYPE.MAILDIR);
        log.info("Created incoming message service: ".concat(incoming.toString()));
    }
    
    public void startJoopzServices() {
        log.info("Starting up incoming/outgoing services...");
        (new Thread(incoming)).start();
        //(new Thread(outgoing)).start();
    }
    
    public void testJoopzService(String serviceType) {
        switch (serviceType) {
            case "incoming":
                incoming.test();
                log.info("Incoming message service test complete");
                break;
            case "outgoing":
                outgoing.test();
                log.info("Outgoing message service test complete");
                break;
            case "both":
                incoming.test();
                log.info("Incoming message service test complete");
                outgoing.test();
                log.info("Outgoing message service test complete");
                break;
            default:
                return;
        }
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        JoopzMessageService mainService = new JoopzMessageService();
        String component;

        if(args.length == 0) {
            // Regular startup
            log.info("Regular JoopzMessagingService startup...");
            mainService.startJoopzServices();        
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("-test")) {
                component = args[1];
                if(component.equalsIgnoreCase("incoming")) {
                    log.info("Running a test on the incoming message service");
                    mainService.testJoopzService("incoming");
                } else if(component.equalsIgnoreCase("outgoing")) {
                    log.info("Running a test on the outgoing message service");
                    mainService.testJoopzService("outgoing");
                } else if(component.equalsIgnoreCase("both")) {
                    log.info("Running a test on both message services");
                    mainService.testJoopzService("both");
                } else {
                    log.warning("Test was specified with unknown component [" + component + "]");
                }
            } else {
                log.warning("Unknown option flag: " + args[0]);
            }
        } else {
            log.warning("Invalid invocation of JoopzMessageService! Invalid argument count: " + args.length);
        }
        log.info("All done!  JoopzMessagingService exiting.");    
    }
}
