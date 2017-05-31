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
        userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Conectado ao servidor. Aguardando autenticação..."));
    }

    @OnMessage
    public void handleMessage(String jsonFrame, Session userSession) throws IOException {
        Frame frame = new Frame(new JSONObject(jsonFrame));
        DecodedJWT token;
        switch (frame.getType()) {
            case "authentication":
                token = verificaToken(frame.getToken());
                if (token != null) {
                    userSession.getUserProperties().put("username", token.getClaim("username").asString());
                    userSession.getUserProperties().put("token", token.getToken());
                    userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Autenticado!"));
                } else {
                    userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Falha na autenticação!"));
                    handleClose(userSession);
                }
                break;
            case "message":
                token = verificaToken((String) userSession.getUserProperties().get("token"));
                if (token != null) {
                    String username = (String) userSession.getUserProperties().get("username");
                    if (frame.getTo().equals("broadcast")) {
                        Iterator<Session> iterator = chatroomUsers.iterator();
                        Session item;
                        while (iterator.hasNext()) {
                            item = iterator.next();
                            boolean sameOrigin = item.getId().equals(userSession.getId());
                            item.getBasicRemote().sendText(buildMessageResponseJson(username, frame.getMessage(),sameOrigin));
                        }
                    } else {

                    }
                } else {
                    userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Falha na autenticação!"));
                    handleClose(userSession);
                }
                break;
            default:
                userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Tipo de frame incorreto"));
                handleClose(userSession);
                break;   
        }
    }

    private DecodedJWT verificaToken(String token) throws IllegalArgumentException, UnsupportedEncodingException {
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

    private String buildMessageResponseJson(String username, String message, boolean sameOrigin) {
        String json = new JSONObject()
                .put("from", username)
                .put("message", message)
                .put("type", "message")
                .put("sameOrigin", sameOrigin)
                .toString();
        return json;
    }
    
    private String buildSystemMessageResponseJson(String message) {
        String json = new JSONObject()
                .put("message", message)
                .put("type", "system")
                .toString();
        return json;
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println();
    }
}
