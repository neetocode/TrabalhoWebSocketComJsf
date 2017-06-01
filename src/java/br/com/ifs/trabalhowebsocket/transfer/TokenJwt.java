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
public class TokenJwt {
    private final String username;
    private final String userid;
    private final String token;
    
    public TokenJwt(String username, String userid, String token) {
        this.username = username;
        this.userid = userid;
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }
    
    public String getToken() {
        return token;
    }
    
}
