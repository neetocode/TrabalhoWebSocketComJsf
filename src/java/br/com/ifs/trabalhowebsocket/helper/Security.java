/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.helper;

import br.com.ifs.trabalhowebsocket.transfer.TokenJwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neetocode
 */
public class Security {
    public static TokenJwt ValidaToken(String token){
         try {
            Algorithm algorithm = Algorithm.HMAC256(Constantes.HASH_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(Constantes.PROJECT)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            TokenJwt tokenValidado = new TokenJwt(
                    jwt.getClaim("username").asString(),
                    jwt.getClaim("userid").asString(), 
                    jwt.getToken());
            return tokenValidado;
        } catch (JWTVerificationException ex) {
            return null; //TOKEN INVÁLIDO
        } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static String GerarToken(String username, String userid){
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_WEEK, 1);
            Algorithm algorithm = Algorithm.HMAC256(Constantes.HASH_KEY);
            String token = JWT.create()
                    .withIssuer(Constantes.PROJECT)
                    .withClaim("username", username)
                    .withClaim("userid", userid)
                    .withExpiresAt(cal.getTime())
                    .sign(algorithm);
            return token;
        } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
