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
public class JoopzIncomingMessageService implements Runnable {
    final static Logger log = Logger.getLogger(JoopzIncomingMessageService.class.getName());
    
    
    public JoopzIncomingMessageService() {
    }

    @Override
    public void run() {
        while(true) {
            try {
                log.info("Sleeping for 3 seconds...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
