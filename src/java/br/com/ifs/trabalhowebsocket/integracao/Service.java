
package br.com.ifs.trabalhowebsocket.integracao;

import br.com.ifs.trabalhowebsocket.bo.UsuarioBo;
import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author neetocode
 */
@Path("login")
public class Service {

    @Context
    private UriInfo context;
    private final UsuarioBo usuarioBo = new UsuarioBo();
   
    public Service() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loginUsuario(Usuario usuario) {
        Usuario autenticado = usuarioBo.AutenticaUsuario(usuario);
        if (autenticado != null) {
            String token = usuarioBo.GerarToken(autenticado);
            return "{\"r\":true,\"data\":{\"token\":\""+token+"\"}}";
        } else {
            return "{\"r\":false}";
        }
    }
}
