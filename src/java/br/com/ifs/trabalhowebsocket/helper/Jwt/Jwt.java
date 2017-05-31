/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.helper.Jwt;

import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.util.Base64;

/**
 *
 * @author neetocode
 */
public class Jwt {
    /*private String typ;
    private String alg;
    
    private String iss;
    private String username;
    private String userid;*/
    
    public String create(String username, int userid, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException{
        JsonObject headerObj = Json.createObjectBuilder()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .build();
        JsonObject payloadObj = Json.createObjectBuilder()
                .add("iss", "br.com.ifs.trabalhowebsocket")
                .add("username", username)
                .add("userid",userid)
                .build();
        
        StringWriter headerJson = new StringWriter();
        try (JsonWriter jsonwriter = Json.createWriter(headerJson)) {
            jsonwriter.write(headerObj);
        }
        StringWriter payloadJson = new StringWriter();
        try (JsonWriter jsonwriter = Json.createWriter(payloadJson)) {
            jsonwriter.write(payloadObj);
        }
        byte[] header = Base64.getEncoder().encode(headerJson.toString().getBytes());
        byte[] payload = Base64.getEncoder().encode(payloadJson.toString().getBytes());
        
        String assinaturaString = HmacSha1Signature.calculateRFC2104HMAC(
                new String(header)+"."+new String(payload), key);
        
        byte[] assinatura = Base64.getEncoder().encode(assinaturaString.getBytes());
        
        return new String(header)+"."+new String(payload)+"."+new String(assinatura);
    }
    
}
