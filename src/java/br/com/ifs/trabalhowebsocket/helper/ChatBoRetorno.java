/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.helper;

import javax.websocket.Session;

/**
 *
 * @author neetocode
 */
public class ChatBoRetorno {
    private Session session;
    private String message;
    
    public ChatBoRetorno(Session session, String message) {
        this.session = session;
        this.message = message;
    }
    
    public Session getSession() {
        return session;
    }

    public String getMessage() {
        return message;
    }
    

    
    
}
