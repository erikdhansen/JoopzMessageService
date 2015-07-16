/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author ehansen
 */
public class DBUtils {
    final static Logger log = Logger.getLogger(DBUtils.class.getName());
    
    final static String LNP_URL = "http://lrn.joopz.com/get.carrier.php?tn=";
    final static String NO_LNP_URL = "http://199.232.41.206/getcarrier.php?p=";
    
    public String getUserPhoneNumber(String userId) throws SQLException {
        Connection c = getConnection();
        Statement st = c.createStatement();
        String phoneNumber = "";
        ResultSet rs = st.executeQuery("SELECT phone_number FROM users WHERE id=" + userId);
        if(rs.next()) {
            phoneNumber = rs.getString(1);
        }
        try {
            c.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);
        }
        return phoneNumber;
    }
    
    public String getContactUniqueId(String contactId) throws SQLException {
        String uniqueId = "";
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT unique_id FROM contacts WHERE id=" + contactId);
        if(rs.next()) {
            uniqueId = rs.getString(1);
        }
        try {
            c.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);            
        }
        return uniqueId;
    }
    
    public String getPartnerDomainForUser(String userId) throws SQLException {
        String domain = "";
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT domain FROM partners WHERE id=(SELECT partner_id FROM users WHERE user_id=" + userId);
        if(rs.next()) {
            domain = rs.getString(1);
        }
        try {
            c.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);
        }
        return domain;
    }
    
    public String getContactPhoneNumber(String contactId) throws SQLException {
        Connection c = getConnection();
        Statement st = c.createStatement();
        String phoneNumber = "";
        ResultSet rs = st.executeQuery("SELECT phone_number FROM contacts WHERE id=" + contactId);
        if(rs.next()) {
            phoneNumber = rs.getString(1);
        }
        try {
            c.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);
        }
        return phoneNumber;        
    }
    
    public String getCarrierSmtpGateway(String phoneNumber) {
        
    }
    
    private String getCarrierNameFromPhoneNumber(String phoneNumber) throws IOException {
        if(phoneNumber.startsWith("1")) {
            phoneNumber = phoneNumber.substring(1);
        }
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(LNP_URL + phoneNumber);
        HttpResponse response = client.execute(get);
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        return r.readLine().toLowerCase();
    }
    
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");            
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/joopz", "joopz", "joopz!");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error getting PostgreSQL connection!", e);
        }
        return connection;
    }
}
