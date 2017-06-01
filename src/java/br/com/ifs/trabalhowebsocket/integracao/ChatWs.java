package br.com.ifs.trabalhowebsocket.integracao;

import br.com.ifs.trabalhowebsocket.bo.ChatBo;
import br.com.ifs.trabalhowebsocket.helper.Security;
import br.com.ifs.trabalhowebsocket.helper.ChatBoRetorno;
import br.com.ifs.trabalhowebsocket.transfer.Frame;
import br.com.ifs.trabalhowebsocket.transfer.UserChat;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

@ServerEndpoint("/chat")
public class ChatWs {

    static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void handleOpen(Session context){
        ChatBo chatBo = new ChatBo(context);
        ChatBoRetorno retorno = chatBo.AddUser();
        send(retorno.getSession(),retorno.getMessage());
    }

    @OnMessage
    public void handleMessage(String jsonFrame, Session userSession) throws IOException {
        Frame frame = new Frame(new JSONObject(jsonFrame));
        DecodedJWT token;
        switch (frame.getType()) {
            case "authentication":
                token = Security.verificaToken(frame.getToken());
                if (token != null) {
                    userSession.getUserProperties().put("username", );
                    userSession.getUserProperties().put("userid", );
                    userSession.getUserProperties().put("token", );
                    userSession.getBasicRemote().sendText(
                            buildAuthenticationResponseJson(
                                    token.getClaim("username").asString(), token.getClaim("userid").asString()));
                    enviaUsuarios();
                } else {
                    userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Falha na autenticação!"));
                    handleClose(userSession);
                }
                break;
            case "message":
                token = verificaToken((String) userSession.getUserProperties().get("token"));
                if (token != null) {
                    String username = (String) userSession.getUserProperties().get("username");
                    String userid = (String) userSession.getUserProperties().get("userid");
                    if (frame.getTo().equals("broadcast")) {
                        Iterator<Session> iterator = users.iterator();
                        Session item;
                        while (iterator.hasNext()) {
                            item = iterator.next();
                            boolean sameOrigin = item.getId().equals(userSession.getId());
                            item.getBasicRemote().sendText(buildMessageResponseJson(username, userid, frame.getMessage(), sameOrigin));
                        }
                    } else {
                        Session destino = getSessionById(frame.getTo());
                        destino.getBasicRemote().sendText(buildMessageResponseJson(username, userid, frame.getMessage(), false));
                        userSession.getBasicRemote().sendText(buildMessageResponseJson(username, frame.getTo(), frame.getMessage(), true));
                    }
                } else {
                    userSession.getBasicRemote().sendText(buildSystemMessageResponseJson("Falha na autenticação!"));
                    handleClose(userSession);
                }
                break;
            
        }
    }

    private void enviaUsuarios() throws IOException {
        Iterator<Session> iterator = users.iterator();
        Session item;
        ArrayList<UserChat> usuariosChat = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            usuariosChat.add(
                    new UserChat(
                            (String) item.getUserProperties().get("username"),
                            (String) item.getUserProperties().get("userid")
                    )
            );
        }

        iterator = users.iterator();
        while (iterator.hasNext()) {
            item = iterator.next();
            item.getBasicRemote().sendText(buildUsersJson(usuariosChat));
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
    public void handleClose(Session userSession) throws IOException {
        users.remove(userSession);
        enviaUsuarios();
    }

    

    private Session getSessionById(String id) {

        Iterator<Session> iterator = users.iterator();
        Session item;
        while (iterator.hasNext()) {
            item = iterator.next();
            String userid = (String) item.getUserProperties().get("userid");
            if (userid.equals(id)) {
                return item;
            }
        }

        return null;
    }
    
    

    @OnError
    public void onError(Throwable t) {
        System.out.println();
    }
    
    private void send(Session destination, String json){
        try {
            destination.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            Logger.getLogger(ChatWs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
