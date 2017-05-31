package br.com.ifs.trabalhowebsocket;

import br.com.ifs.trabalhowebsocket.transfer.Frame;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint("/chat")
public class Chat {

    static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void handleOpen(Session userSession) throws IOException {
        chatroomUsers.add(userSession);
        userSession.getBasicRemote().sendText(buildMessageResponseJson("", "Conectado ao servidor. Aguardando autenticação...", "system"));
    }

    @OnMessage
    public void handleMessage(String jsonFrame, Session userSession) throws IOException {
        Frame frame = new Frame(new JSONObject(jsonFrame));
        switch(frame.getType()){
            case "authentication":
                DecodedJWT token = verificaToken(frame.getToken());
                if(token != null){
                    userSession.getUserProperties().put("username", token.getClaim("username"));
                    userSession.getUserProperties().put("token", token.getToken());
                    userSession.getBasicRemote().sendText(buildMessageResponseJson("", "Autenticado!","system"));
                }else{
                    handleClose(userSession);
                }
                break;
            case "message":
                String username = (String) userSession.getUserProperties().get("token");
                break;
        }
        
        
        if(userMessage.getType().equals("autentication")){
            userSession.getUserProperties().put("username", userMessage.getUsername());
            
            
        }
        if(userMessage.getType().equals("message")){
            String username = (String) userSession.getUserProperties().get("username");

            if (username == null) { // verifique se é a primeira mensagem e seta o username na sessão do WS
                userSession.getUserProperties().put("username", userMessage.getUsername());
                username = userMessage.getUsername();
            }
            if(userMessage.getTo().equals("broadcast")){
                Iterator<Session> iterator = chatroomUsers.iterator();
                while (iterator.hasNext()) {
                    iterator.next().getBasicRemote().sendText(buildMessageResponseJson(username, userMessage.getMessage(), "message"));
                }
            }else{

            }
        }

    
    private DecodedJWT verificaToken(String token) throws IllegalArgumentException, UnsupportedEncodingException{
        try {
            Algorithm algorithm = Algorithm.HMAC256("batata-doce");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("br.com.ifs.trabalhowebsocket")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    @OnClose
    public void handleClose(Session userSession) {
        chatroomUsers.remove(userSession);
    }

    private String buildMessageResponseJson(String username, String message, String type) {
        String json = new JSONObject()
                .put("from", username)
                .put("message", message)
                .put("type", type)
                .toString();
        return json;
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println();
    }
}
