//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

/**
 *
 * @author rolbi
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:postgresql://ep-shrill-rain-a45o2uk1-pooler.us-east-1.aws.neon.tech/universidad";
    private static final String USUARIO = "neondb_owner";
    private static final String CONTRASENA = "npg_uoMzDn26CvqJ";

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador de PostgreSQL", e);
        }
    }
}