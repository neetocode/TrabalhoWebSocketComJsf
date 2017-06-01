/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.transfer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author neetocode
 */
public class Frame {
    private String message;
    private String destination;
    private String username;
    private final String type;
    private String token;
    public final static String TYPE_AUTHENTICATION = "authentication";
    public final static String TYPE_MESSAGE = "message";
    public final static String TYPE_SYSTEM = "system";
    public final static String TYPE_USERS = "users";
    
    public Frame(JSONObject jsonObject){
        this.type = jsonObject.getString("type");
        if(this.type.equals(TYPE_AUTHENTICATION)){
            this.token = jsonObject.getString("token");
        }
        if(this.type.equals(TYPE_MESSAGE)){
            this.message = jsonObject.getString("message");
            this.destination = jsonObject.getString("to");
        }
    }
    
    public String getType() {
        return type;
    }
    public String getUsername() {
        return username;
    }
    public String getMessage() {
        return message;
    }
    public String getToken() {
        return token;
    }
    public String getTo() {
        return destination;
    }  
}
