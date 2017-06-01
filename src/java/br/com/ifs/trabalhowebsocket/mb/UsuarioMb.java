/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.mb;

import br.com.ifs.trabalhowebsocket.helper.CookieHelper;
import br.com.ifs.trabalhowebsocket.helper.Security;

import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author neetocode
 */
@ManagedBean
public class UsuarioMb {

    private Usuario user = new Usuario();
    
    public String logar(){
        
        if (
                user.getNome().equals("Danilo Souza") && user.getSenha().equals("123") ||
                user.getNome().equals("Miguel Neto") && user.getSenha().equals("123") ||
                user.getNome().equals("Alexandre Carlo") && user.getSenha().equals("123")
                ) {
            
            if(user.getNome().equals("Danilo Souza")) user.setId(1);
            if(user.getNome().equals("Miguel Neto")) user.setId(2);
            if(user.getNome().equals("Alexandre Carlo")) user.setId(3);
            
            String token = Security.GerarToken(user.getNome(), Integer.toString(user.getId()));
            CookieHelper cookieHelper = new CookieHelper();
            cookieHelper.setCookie("token", token, Integer.MAX_VALUE);
            return "index?faces-redirect=true";
        } else {
            return "login?faces-redirect=true";
        }

    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

}
