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

    public UserChat(String username, String id) {
        this.username = username;
        this.id = id;
    }
    
    
    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
