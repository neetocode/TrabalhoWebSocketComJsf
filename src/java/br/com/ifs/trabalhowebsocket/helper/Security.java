/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.helper;

import br.com.ifs.trabalhowebsocket.transfer.TokenJwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neetocode
 */
public class Security {

    public static TokenJwt ValidaToken(String token) {

        //Algorithm algorithm = Algorithm.HMAC256(Constantes.HASH_KEY);
        //JWTVerifier verifier = JWT.require(algorithm)
        //.withIssuer(Constantes.PROJECT)
        //      .build();
        //DecodedJWT jwt = verifier.verify(token);
        //DecodedJWT jwt = JWT.decode("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE");
        //verifier.verify(token)
        Jws<Claims> claims = Jwts.parser().setSigningKey(Constantes.HASH_KEY).parseClaimsJws(token);
        claims.getBody().get("username");
        TokenJwt tokenValidado = new TokenJwt(
                //jwt.getClaim("username").asString(),
                //jwt.getClaim("userid").asString(), 
                //jwt.getToken()
                claims.getBody().get("username").toString(),
                claims.getBody().get("userid").toString(),
                token
        );
        return tokenValidado;

    }

    public static String GerarToken(String username, String userid) {
        try {
            Key key = MacProvider.generateKey();
            //Calendar cal = Calendar.getInstance();
            //cal.setTime(new Date());
            //cal.add(Calendar.DAY_OF_WEEK, 1);
            //Algorithm algorithm = Algorithm.HMAC256(Constantes.HASH_KEY);
            String token = Jwts.builder()
                    //.withIssuer(Constantes.PROJECT)
                    .claim("username", username)
                    .claim("userid", userid)
                    .signWith(SignatureAlgorithm.HS512, Constantes.HASH_KEY)
                    .compact();

            return token;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
