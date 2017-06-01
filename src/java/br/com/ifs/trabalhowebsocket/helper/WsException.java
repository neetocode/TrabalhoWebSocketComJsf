package br.com.ifs.trabalhowebsocket.helper;

/**
 *
 * @author neetocode
 */
public class WsException extends Exception{
    private final String json;
    private final boolean shouldClose;
    
    public WsException(String json) {
        this.json = json;
        this.shouldClose = false;
    }
    
    public WsException(String json, boolean shouldClose) {
        this.json = json;
        this.shouldClose = shouldClose;
    }
    
    public String getJson(){
        return this.json;
    }
    
    public boolean shouldClose(){
        return this.shouldClose;
    }
    
    
}
