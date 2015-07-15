/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

/**
 *
 * @author ehansen
 */
class JoopzMessageServiceException extends Exception {
    
    public JoopzMessageServiceException(String message) {
        super(message);
    }
    
    public JoopzMessageServiceException(String message, Throwable t) {
        super(message, t);
    }
}
