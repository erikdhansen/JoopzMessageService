/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;

/**
 *
 * @author ehansen
 */
public class JoopzOutgoingMessageService implements Runnable {
    final static Logger log = Logger.getLogger(JoopzOutgoingMessageService.class.getName());
    
    final static String PG_URL  = "jdbc:postgresql://localhost/joopz";
    final static String PG_USER = "joopz";
    final static String PG_PASS = "joopz!";
    
    Jedis redis;
    Connection pgsql;
    
    JoopzEmailService emailService = new JoopzEmailService();
    
    public JoopzOutgoingMessageService() throws JoopzMessageServiceException {
        redis = new Jedis("localhost");
        log.info("Redis server status: ".concat(redis.ping()));
        
        if(!redis.isConnected()) {
            throw new JoopzMessageServiceException("Redis database is not connected!");
        }
        
        pgsql = null;
        try {
            pgsql = DriverManager.getConnection(PG_URL, PG_USER, PG_PASS);
            if(pgsql.isValid(10)) {
                log.info("PostgreSQL server is up!");
            } else {
                throw new JoopzMessageServiceException("Unable to communicate with Postgres database! Connection state is invalid!");
            }
        } catch (SQLException e) {
            throw new JoopzMessageServiceException("Unable to communicate with Postgres database!", e);
        }
        
    }
    
    
    @Override
    public void run() {
        long queueSize = redis.llen(RedisUtil.MESSAGES_OUTGOING);
        log.info("Redis Queue[" .concat(RedisUtil.MESSAGES_OUTGOING).concat("]: size = ").concat(Long.toString(queueSize)));

        while(true) {
            List<String> queueInfo = redis.brpop(0, RedisUtil.MESSAGES_OUTGOING);
            String queueName = queueInfo.get(0);
            for(int i=1; i < queueInfo.size(); i++) {            
                try {
                    String value = queueInfo.get(i);
                    JoopzOutgoingMessage msg = new JoopzOutgoingMessage(value);
                    log.info("Pulled JoopzOutgoingMessage from queue".concat(queueName));
                    log.info("Msg: ".concat(msg.toString()).concat("\n"));    
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Caught Exception: " + e.getClass().getSimpleName(), e);
                    log.info("Resume listening.");
                }
            }
        }
        
    }
    
}
