/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 *
 * @author ehansen
 */
public class JoopzOutgoingMessage {
        
    String contactId = null;
    String groupId = null;
    String userId = null;
    String message = null;
    
    String jsonString = null;
    JsonReader json;
    JsonObjectBuilder jsonBuilder;
    JsonObject jsonObj = null;
    
    public JoopzOutgoingMessage() {
        
    }

    
    public JoopzOutgoingMessage(String jsonEncoded) {
        jsonString = jsonEncoded;
        json = JsonUtils.getJsonReader(jsonEncoded);    
        
        JsonObject obj = json.readObject();
        contactId = obj.getString("contact_id");
        groupId = obj.getString("group_id");
        userId = obj.getString("user_id");
        message = obj.getString("message");
    }

    public String toJson() {
        if(jsonString == null) {
            jsonBuilder = JsonUtils.getJsonBuilder();
            jsonBuilder.add("user_id", userId);
            if(groupId != null)
                jsonBuilder.add("group_id", groupId);
            if(contactId != null)
                jsonBuilder.add("contact_id", contactId);
            jsonBuilder.add("message", message);
        
            jsonObj = jsonBuilder.build();
            jsonString = jsonObj.toString();
        }
        return jsonString;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "OutgoingMessage={ user_id => " + userId + ", contact_id => " +
                contactId + ", group_id => " + groupId + ", message => " + 
                message + " }";  
    }
    
}
