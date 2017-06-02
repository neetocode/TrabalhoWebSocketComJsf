package br.com.ifs.trabalhowebsocket.dao;

import br.com.ifs.trabalhowebsocket.db.ConnectionFactory;
import br.com.ifs.trabalhowebsocket.transfer.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author neetocode
 */
public class UsuarioDao {
    public Usuario AutenticaUsuario(String userName, String senha){
        String sql = "SELECT * FROM usuario WHERE username = ? AND senha = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.createConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, senha);
            rs = ps.executeQuery();
            if(rs.next()){
                Usuario usuario = new Usuario(rs.getInt("id"),rs.getString("username"));
                return usuario;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {            
            try {
                if(rs != null) rs.close();
                if(con != null) con.close();
                if(ps != null) ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsuarioDao.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
        return null;
    }
    
    public ArrayList<Usuario> GetAllUsuarios(){
        String sql = "SELECT id, username FROM usuario";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Usuario> retorno = new ArrayList<>();
        try {
            con = ConnectionFactory.createConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while(rs.next()){
                retorno.add(new Usuario(rs.getInt("id"),rs.getString("username")));
            }
            return retorno;
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(rs != null) rs.close();
                if(con != null) con.close();
                if(ps != null) ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsuarioDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retorno;
    }
}
