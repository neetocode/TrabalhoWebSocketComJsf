package br.com.ifs.trabalhowebsocket.bo;

import br.com.ifs.trabalhowebsocket.dao.UsuarioDao;
import br.com.ifs.trabalhowebsocket.helper.ChatBoRetorno;
import br.com.ifs.trabalhowebsocket.helper.Security;
import br.com.ifs.trabalhowebsocket.exceptions.WsException;
import br.com.ifs.trabalhowebsocket.transfer.Frame;
import br.com.ifs.trabalhowebsocket.transfer.TokenJwt;
import br.com.ifs.trabalhowebsocket.transfer.UserChat;
import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neetocode
 */
public class ChatBo {
    private static final Set<Session> USERS = Collections.synchronizedSet(new HashSet<Session>());
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final Session context;
    
    public ChatBo(Session context){
        this.context = context;
    }
        
    public ArrayList<ChatBoRetorno> Autentica(String tokenString) throws WsException{
        ArrayList<ChatBoRetorno> retorno = new ArrayList<>();
        TokenJwt token = Security.ValidaToken(tokenString);
        if(token == null) throw new WsException(buildSystemJson("Falha na autenticação"),true);
                
        this.context.getUserProperties().put("username", token.getUsername());
        this.context.getUserProperties().put("userid", token.getUserid());
        this.context.getUserProperties().put("token", token.getToken());
        ChatBo.USERS.add(this.context);
        retorno.add(new ChatBoRetorno(this.context, buildAuthenticationJson(token.getUsername(), token.getUserid())));
        enviaUsuarios(retorno);
        
        return retorno;
    }
    
    public ArrayList<ChatBoRetorno> FrameHandler(Frame frame) throws Exception{
        ArrayList<ChatBoRetorno> retorno = new ArrayList<>();
        TokenJwt token;
        switch(frame.getType()){
            /*case Frame.TYPE_AUTHENTICATION:
                token = Security.ValidaToken(frame.getToken());
                if(token == null) throw new WsException(buildSystemJson("Falha na autenticação"),true);
                
                this.context.getUserProperties().put("username", token.getUsername());
                this.context.getUserProperties().put("userid", token.getUserid());
                this.context.getUserProperties().put("token", token.getToken());
                retorno.add(new ChatBoRetorno(this.context, buildAuthenticationJson(token.getUsername(), token.getUserid())));
                enviaUsuarios(retorno);
                
                break;*/
            case Frame.TYPE_MESSAGE:
                token = Security.ValidaToken((String) this.context.getUserProperties().get("token"));
                if(token == null) throw new WsException(buildSystemJson("Falha na autenticação"),true);
                
                UserChat userRemetente = new UserChat((String) this.context.getUserProperties().get("username"),(String) this.context.getUserProperties().get("userid"));
                
                Session destino = getSessionByUserId(frame.getTo());
                
                if(destino != null){
                    UserChat userDestino = new UserChat((String) destino.getUserProperties().get("username"),(String) destino.getUserProperties().get("userid"));
                    
                    retorno.add(new ChatBoRetorno(destino,buildMessageJson(userRemetente.getUsername(), userRemetente.getId(), frame.getMessage(), false)));
                    retorno.add(new ChatBoRetorno(this.context,buildMessageJson(userRemetente.getUsername(), userDestino.getId(), frame.getMessage(), true)));
                }else{
                    retorno.add(new ChatBoRetorno(this.context,buildSystemJson("Usuário não disponível.")));
                }
                break;
            default:
                throw new WsException(buildSystemJson("Tipo de frame incorreto"));
        }
        return retorno;
    }
    
    public ArrayList<ChatBoRetorno> removerUser(){
        ChatBo.USERS.remove(this.context);
        return enviaUsuarios(new ArrayList<>());
    }
    
    public ArrayList<ChatBoRetorno> enviaUsuarios(ArrayList<ChatBoRetorno> retorno){
        ArrayList<Usuario> cadastrados = usuarioDao.GetAllUsuarios();
        Iterator<Session> iterator = USERS.iterator();
        ArrayList<UserChat> usuariosChat = new ArrayList<>();
        Session item;
        cadastrados.forEach((Usuario cadastrado) ->{
            boolean online = false;
            for(Session wsUser : USERS){
                String wsUserId = (String) wsUser.getUserProperties().get("userid");
                if(wsUserId.equals(Integer.toString(cadastrado.getId()))){
                    online = true;
                    break;
                }
            }
            usuariosChat.add(new UserChat(cadastrado.getNome(),Integer.toString(cadastrado.getId()),online));
        });
        
        return broadcast(retorno,buildUsersJson(usuariosChat));
    }
    
    private ArrayList<ChatBoRetorno> broadcast(ArrayList<ChatBoRetorno> retorno, String message){
        Iterator<Session> iterator = USERS.iterator();
        Session item;
        while (iterator.hasNext()) {
            item = iterator.next();
            retorno.add(new ChatBoRetorno(item, message));
        }
        return retorno;
    }
    
    
    
    
    private Session getSessionByUserId(String id) {

        Iterator<Session> iterator = USERS.iterator();
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
