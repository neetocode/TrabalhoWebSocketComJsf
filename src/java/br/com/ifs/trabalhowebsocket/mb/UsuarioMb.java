package br.com.ifs.trabalhowebsocket.mb;

import br.com.ifs.trabalhowebsocket.bo.UsuarioBo;

import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author neetocode
 */
@ManagedBean
public class UsuarioMb {
    private Usuario user = new Usuario();
    private final UsuarioBo usuarioBo = new UsuarioBo();
    
    public String login(){
        Usuario autenticado = usuarioBo.AutenticaUsuario(user);
        if (autenticado != null) {
            usuarioBo.GerarESalvarToken(autenticado);
            return "chat?faces-redirect=true";
        } else {

            return null;
        }

    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
    
    

}
