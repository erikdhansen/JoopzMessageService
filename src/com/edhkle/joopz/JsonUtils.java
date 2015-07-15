/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edhkle.joopz;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/**
 *
 * @author ehansen
 */
public class JsonUtils {
    
    public static JsonReader getJsonReader(String jsonString) {
        return Json.createReader(new StringReader(jsonString));
    }
    
    public static JsonObjectBuilder getJsonBuilder() {
        return Json.createObjectBuilder();
    }
}
