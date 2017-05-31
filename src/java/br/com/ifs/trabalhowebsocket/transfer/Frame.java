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
    private String to;
    private String username;
    private final String type;
    private String token;
    
    public Frame(JSONObject jsonObject){
        this.type = jsonObject.getString("type");
        if(this.type.equals("authentication")){
            this.token = jsonObject.getString("token");
        }
        if(this.type.equals("message")){
            this.message = jsonObject.getString("message");
            this.to = jsonObject.getString("to");
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
        return to;
    }  
}
