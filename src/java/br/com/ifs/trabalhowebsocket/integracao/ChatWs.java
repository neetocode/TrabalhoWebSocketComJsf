package br.com.ifs.trabalhowebsocket.integracao;

import br.com.ifs.trabalhowebsocket.bo.ChatBo;
import br.com.ifs.trabalhowebsocket.helper.ChatBoRetorno;
import br.com.ifs.trabalhowebsocket.helper.WsException;
import br.com.ifs.trabalhowebsocket.transfer.Frame;
import java.io.IOException;
import java.util.ArrayList;
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
    public void handleOpen(Session context){
        ChatBo chatBo = new ChatBo(context);
        ChatBoRetorno retorno = chatBo.AddUser();
        send(retorno.getSession(),retorno.getMessage());
    }

    @OnMessage
    public void handleMessage(String jsonFrame, Session context) throws IOException {
        try {
            Frame frame = new Frame(new JSONObject(jsonFrame));
            ChatBo chatBo = new ChatBo(context);
            ArrayList<ChatBoRetorno> retornos = chatBo.FrameHandler(frame);
            retornos.forEach((retorno) -> {
                send(retorno.getSession(), retorno.getMessage());
            });
            
        } catch (WsException ex) {
            send(context,ex.getJson());
            if(ex.shouldClose()){
                handleClose(context);
            }
        } catch (Exception ex) {
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
}
