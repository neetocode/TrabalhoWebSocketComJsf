package br.com.ifs.trabalhowebsocket.integracao;

import br.com.ifs.trabalhowebsocket.bo.ChatBo;
import br.com.ifs.trabalhowebsocket.helper.ChatBoRetorno;
import br.com.ifs.trabalhowebsocket.exceptions.WsException;
import br.com.ifs.trabalhowebsocket.helper.QueryString;
import br.com.ifs.trabalhowebsocket.transfer.Frame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint("/chat")
public class ChatWs {
    @OnOpen
    public void handleOpen(Session context) throws IOException{
        try {
            Map<String, String> queryString = QueryString.decode(context.getQueryString());
            ChatBo chatBo = new ChatBo(context);
            ArrayList<ChatBoRetorno> retornos = chatBo.Autentica(queryString.get("t"));
            
            send(retornos);
        } catch (WsException ex) {
            send(context,ex.getJson());
            if(ex.shouldClose()){ // verifica se a exception fecha o websocket
                handleClose(context);
            }
        }
    }

    @OnMessage
    public void handleMessage(String jsonFrame, Session context) throws IOException {
        try {
            Frame frame = new Frame(new JSONObject(jsonFrame));
            ChatBo chatBo = new ChatBo(context);
            ArrayList<ChatBoRetorno> retornos = chatBo.FrameHandler(frame);

            send(retornos);
            
        } catch (WsException ex) {
            send(context,ex.getJson());
            if(ex.shouldClose()){ // verifica se a exception fecha o websocket
                handleClose(context);
            }
        } catch (Exception ex) {
            send(context,ex.getMessage());
            Logger.getLogger(ChatWs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @OnClose
    public void handleClose(Session context) throws IOException {
        ChatBo chatBo = new ChatBo(context);
        ArrayList<ChatBoRetorno> retornos = chatBo.removerUser();
        retornos.forEach((retorno) -> {
            send(retorno.getSession(), retorno.getMessage());
        });
        context.close();
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
    
    private void send(ArrayList<ChatBoRetorno> retornos){
        retornos.forEach((retorno) -> {
            send(retorno.getSession(), retorno.getMessage());
        });
    }
}
