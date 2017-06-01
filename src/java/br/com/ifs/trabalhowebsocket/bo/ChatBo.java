package br.com.ifs.trabalhowebsocket.bo;

import br.com.ifs.trabalhowebsocket.helper.ChatBoRetorno;
import br.com.ifs.trabalhowebsocket.helper.Security;
import br.com.ifs.trabalhowebsocket.integracao.ChatWs;
import br.com.ifs.trabalhowebsocket.transfer.Frame;
import br.com.ifs.trabalhowebsocket.transfer.TokenJwt;
import br.com.ifs.trabalhowebsocket.transfer.UserChat;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neetocode
 */
public class ChatBo {
    private static final Set<Session> USERS = Collections.synchronizedSet(new HashSet<Session>());
    private final Session context;
    
    public ChatBo(Session context){
        this.context = context;
    }
    
    public ChatBoRetorno AddUser(){
        ChatBo.USERS.add(this.context);
        return new ChatBoRetorno(this.context, "Conectado a o servidor, aguardando autenticação...");
    }
    
    public ArrayList<ChatBoRetorno> FrameHandler(Frame frame) throws Exception{
        ArrayList<ChatBoRetorno> retorno = new ArrayList<>();
        TokenJwt token;
        switch(frame.getType()){
            case Frame.TYPE_AUTHENTICATION:
                token = Security.ValidaToken(frame.getToken());
                if(token != null){
                    this.context.getUserProperties().put("username", token.getUsername());
                    this.context.getUserProperties().put("userid", token.getUserid());
                    this.context.getUserProperties().put("token", token.getToken());
                    retorno.add(new ChatBoRetorno(this.context, buildAuthenticationJson(token.getUsername(), token.getUserid())));
                }else{
                    throw new Exception(buildSystemJson("Falha na autenticação"));
                }
                break;
            case Frame.TYPE_MESSAGE:
                break;
            default:
                throw new Exception("Tipo de frame incorreto");
        }
        return retorno;
    }
    
    
    private void enviaUsuarios(){
        Iterator<Session> iterator = USERS.iterator();
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
    
    
    
    
    
    

    private String buildUsersJson(ArrayList<UserChat> users) {
        String json = new JSONObject()
                .put("data", new JSONArray(users).toString())
                .put("type", "users")
                .toString();
        return json;
    }
    private String buildMessageJson(String username, String id, String message, boolean sameOrigin) {
        String json = new JSONObject()
                .put("userFrom", new JSONObject(new UserChat(username, id)))
                .put("message", message)
                .put("type", "message")
                .put("sameOrigin", sameOrigin)
                .toString();
        return json;
    }
    private String buildSystemJson(String message) {
        String json = new JSONObject()
                .put("message", message)
                .put("type", "system")
                .toString();
        return json;
    }
    private String buildAuthenticationJson(String username, String id) {
        String json = new JSONObject()
                .put("username", username)
                .put("id", id)
                .put("message", "Autenticado")
                .put("type", "authentication")
                .toString();
        return json;
    }
}
