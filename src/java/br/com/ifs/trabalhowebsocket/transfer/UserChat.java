/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.transfer;

/**
 *
 * @author neetocode
 */
public class UserChat {
    private final String username;
    private final String id;
    private final boolean online;

    public UserChat(String username, String id) {
        this.username = username;
        this.id = id;
        this.online = false;
    }
    
    public UserChat(String username, String id, boolean online){
        this.username = username;
        this.id = id;
        this.online = online;
    }
    
    public boolean getOnline() {
        return this.online;
    }
    
    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
