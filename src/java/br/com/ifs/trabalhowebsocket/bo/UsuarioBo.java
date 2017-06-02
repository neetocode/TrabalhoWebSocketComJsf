package br.com.ifs.trabalhowebsocket.bo;

import br.com.ifs.trabalhowebsocket.dao.UsuarioDao;
import br.com.ifs.trabalhowebsocket.helper.CookieHelper;
import br.com.ifs.trabalhowebsocket.helper.Security;
import br.com.ifs.trabalhowebsocket.transfer.Usuario;

/**
 *
 * @author neetocode
 */
public class UsuarioBo {
    private final UsuarioDao usuarioDao = new UsuarioDao();
    
    public Usuario AutenticaUsuario(Usuario usuario){
        Usuario autenticado = usuarioDao.AutenticaUsuario(usuario.getNome(), usuario.getSenha());
        return autenticado;
    }
    
    public void GerarESalvarToken(Usuario usuario){
        String token = Security.GerarToken(usuario.getNome(), Integer.toString(usuario.getId()));
        CookieHelper cookieHelper = new CookieHelper();
        cookieHelper.setCookie("token", token, 7200);
    }
}
