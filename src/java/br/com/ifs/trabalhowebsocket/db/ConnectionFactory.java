package br.com.ifs.trabalhowebsocket.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neetocode
 */
public class ConnectionFactory {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/TrabalhoWebSocket";
    private static final String USER = "postgres";
    private static final String PASS = "senha123";

    public static Connection createConnection(){
        try {
            Class.forName(DRIVER);
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
