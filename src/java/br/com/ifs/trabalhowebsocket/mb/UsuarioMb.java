/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.mb;

import br.com.ifs.trabalhowebsocket.helper.CookieHelper;

import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author neetocode
 */
@ManagedBean
public class UsuarioMb {

    private Usuario user = new Usuario();

    public String logar() throws IllegalArgumentException, UnsupportedEncodingException{
        
        if (user.getNome().equals("Viado") && user.getSenha().equals("boiola") ||
            user.getNome().equals("Miguel") && user.getSenha().equals("123")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_WEEK, 1);
            Algorithm algorithm = Algorithm.HMAC256("batata-doce");
            String token = JWT.create()
                    .withIssuer("br.com.ifs.trabalhowebsocket")
                    .withClaim("username", user.getNome())
                    .withExpiresAt(cal.getTime())
                    .sign(algorithm);
            
            CookieHelper cookieHelper = new CookieHelper();
            cookieHelper.setCookie("token",token,Integer.MAX_VALUE);
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
