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
import java.util.HashMap;
import java.util.Map;
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
    
    final static Map<String,Integer> cMap = new HashMap<>();
    static {
        cMap.put("verizon", 1);
        cMap.put("sprint", 2);
        cMap.put("cingular", 3);
        cMap.put("at&t", 3);
        cMap.put("att", 3);
        cMap.put("uscc", 4);
        cMap.put("alltel", 5);
        cMap.put("t-mobile", 7);
        cMap.put("tmobile", 7);
        cMap.put("cellularone", 9);
        cMap.put("cellular one", 9);
        cMap.put("cricket", 11);
        cMap.put("century", 12);
        cMap.put("metro", 15);
        cMap.put("boost", 19);
    }
    
    public static String getUserPhoneNumber(String userId) throws SQLException {
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
    
    public static String getContactUniqueId(String contactId) throws SQLException {
        String uniqueId = "";
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT uniq_id FROM contacts WHERE id=" + contactId);
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
    
    public static String getPartnerDomainForUser(String userId) throws SQLException {
        String domain = "";
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT domain FROM partners WHERE id=(SELECT partner_id FROM users WHERE id=" + userId + ")");
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
    
    public static String getContactPhoneNumber(String contactId) throws SQLException {
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
    
    public static String getCarrierSmtpGateway(String phoneNumber) throws IOException, SQLException {
        String carrierName = getCarrierNameFromPhoneNumber(phoneNumber);
        log.info("Looking up SMTP gateway for carrier[" + phoneNumber + "]: " + carrierName);
        
        int gwId = lookupCarrierStatic(carrierName);
        if(gwId == 0) {
            gwId = lookupCarrierPostgres(carrierName);
        }
     
        String gateway = "";        
        if(gwId > 0) {
            Connection c = getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT hostname FROM gateways WHERE id=" + String.valueOf(gwId));
            if(rs.next()) {
                gateway = rs.getString(1);
            }
            try {
                c.close();
            } catch (SQLException e) {
                log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);                
            }
        }            
        return gateway;
    }
    
    private static int lookupCarrierStatic(String carrier) {
        int gwId = 0;
        for(String key : cMap.keySet()) {
            if(carrier.toLowerCase().contains(key)) {
                gwId = cMap.get(key);
                break;
            }
        }
        return gwId;
    }
    
    private static int lookupCarrierPostgres(String carrier) throws SQLException {
        int gwId = 0;
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT gateway_id FROM carriers WHERE LOWER(name) like '%" + carrier + "%'");
        if(rs.next()) {
            gwId = rs.getInt(1);
        }
        try {
            c.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQLException closing connection: " + e.getMessage(), e);                            
        }
        return gwId;
    }
    
    private static String getCarrierNameFromPhoneNumber(String phoneNumber) throws IOException {
        if(phoneNumber.startsWith("1")) {
            phoneNumber = phoneNumber.substring(1);
        }
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(LNP_URL + phoneNumber);
        HttpResponse response = client.execute(get);
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
        String carrier = r.readLine();
        while( carrier.length() == 0 )
            carrier = r.readLine();
        
        log.info("readline: " + carrier);
        if(carrier.length() == 0 || carrier.contains("not ported")) {
            return getCarrierNameFromPhoneNumberNonLNP(phoneNumber);
        }
        return carrier.toLowerCase().replaceAll("\\n", "");
    }
    
    private static String getCarrierNameFromPhoneNumberNonLNP(String phoneNumber) throws IOException {
        // 1 should already have been stripped since this is never called directly
        
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(NO_LNP_URL + phoneNumber);
        HttpResponse response = client.execute(get);
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
        String carrier = r.readLine();
        if(carrier.equals("NO")) {
            carrier = "";
        } else {
            carrier = carrier.toLowerCase();
        }
        return carrier;
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
    
    public static Map<String,String> getContactFromUniqueId(String uniqueId) throws SQLException {
        Map<String,String> data = new HashMap<>();
        Connection c = getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT id,user_id,phone_number,uniq_id,name,deleted FROM contacts WHERE uniq_id='" + uniqueId + "'");
        if(rs.next()) {
            data.put("id", String.valueOf(rs.getInt("id")));
            data.put("user_id", String.valueOf(rs.getInt("user_id")));
            data.put("phone_number", rs.getString("phone_number"));
            data.put("uniq_id", rs.getString("uniq_id"));
            data.put("name", rs.getString("name"));
            data.put("deleted", String.valueOf(rs.getBoolean("deleted")));
        }
        log.info("getContactFromUniqueId: uniqueId=" + uniqueId + " contact info=" + data.toString());
        return data;
    }
}
