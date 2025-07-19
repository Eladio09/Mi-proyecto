//Si logras resolverlo, dime como al 37767101.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas :.(
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class LoginController implements Initializable {

    @FXML
    private TextField txtNombre;
    
    @FXML
    private PasswordField txtContrasena;
    
    @FXML
    private ComboBox<String> cbRoles;
    
    @FXML
    private Button btnIngresar;
    
    @FXML
    private Button btnLimpiar;
    
    private static final Map<String, String> USUARIOS = new HashMap<>();
    private static final Map<String, String> ROLES = new HashMap<>();
    
    static {
        crearArchivoRolesConEjemplos();
        
        cargarUsuariosDesdeArchivo();
        
        HistorialLogger.registrarAccion("Sistema", "Inicialización de usuarios y roles completada");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbRoles.getItems().addAll("Admin", "Tecnico", "Usuario");
        cbRoles.setValue("Usuario"); 
        
        HistorialLogger.registrarAccion("Login", "Pantalla de inicio de sesión inicializada");
    }
    
    private boolean autenticarUsuarioDesdeBD(String usuario, String contrasena, String rolSeleccionado) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean autenticado = false;

    try {
        //clase Conexion para obtener la conexión
        conn = Conexion.obtenerConexion();
        
        String sql = "SELECT \"Rol\" FROM public.\"Login\" WHERE \"Usuario\" = ? AND \"Contraseña\" = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, usuario);
        stmt.setString(2, contrasena);
        
        rs = stmt.executeQuery();
        
        if (rs.next()) {
            String rolBD = rs.getString("Rol");
            autenticado = rolBD.equalsIgnoreCase(rolSeleccionado);
            
            if (autenticado) {
                USUARIOS.put(usuario, contrasena);
                ROLES.put(usuario, rolBD);
                HistorialLogger.registrarAccion("Login", "Usuario autenticado desde BD: " + usuario);
            }
        }
    } catch (SQLException e) {
        HistorialLogger.registrarAccion("Login", 
            "Error al autenticar desde BD: " + e.getMessage());
        System.err.println("Error en autenticación BD: " + e.getMessage());
        
        autenticado = autenticarUsuario(usuario, contrasena, rolSeleccionado);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {  }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {  }
        try { if (conn != null) conn.close(); } catch (SQLException e) {  }
    }
    
    return autenticado;
}
    
    private static void crearArchivoRolesConEjemplos() {
        try {
            File archivo = new File("roles.txt");
            
            if (!archivo.exists()) {
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write("admin|admin123|Admin\n");
                    writer.write("tecnico|tecnico123|Tecnico\n");
                    writer.write("usuario|usuario123|Usuario\n");
                    
                    writer.write("\n# Formato: usuario|contraseña|rol\n");
                    writer.write("# Roles válidos: Admin, Tecnico, Usuario\n");
                }
                
                HistorialLogger.registrarAccion("Sistema", "Archivo roles.txt creado con datos de ejemplo");
            }
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Sistema", 
                "Error al crear archivo roles.txt: " + e.getMessage());
            System.err.println("Error al crear archivo roles.txt: " + e.getMessage());
        }
    }
    
    private static void cargarUsuariosDesdeArchivo() {
        try {
            List<String> lineas = Files.readAllLines(Paths.get("roles.txt"));
            
            for (String linea : lineas) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                
                String[] partes = linea.split("\\|");
                if (partes.length == 3) {
                    String usuario = partes[0].trim();
                    String contrasena = partes[1].trim();
                    String rol = partes[2].trim();
                    
                    // Validar rol
                    if (rol.equals("Admin") || rol.equals("Tecnico") || rol.equals("Usuario")) {
                        USUARIOS.put(usuario, contrasena);
                        ROLES.put(usuario, rol);
                    }
                }
            }
            
            HistorialLogger.registrarAccion("Sistema", 
                "Usuarios cargados desde roles.txt. Total: " + USUARIOS.size());
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Sistema", 
                "Error al leer archivo roles.txt: " + e.getMessage());
            System.err.println("Error al leer archivo roles.txt: " + e.getMessage());
        }
    }
    
    @FXML
    private void eventLimpiar(ActionEvent event) {
        HistorialLogger.registrarAccion("Login", "Campos de inicio de sesión limpiados");
        txtNombre.setText("");  
        txtContrasena.setText(""); 
        cbRoles.setValue("Usuario");
        txtNombre.requestFocus();
    }
    
    @FXML
private void eventIngresar(ActionEvent event) {
    String nombre = txtNombre.getText();
    String contrasena = txtContrasena.getText();
    String rolSeleccionado = cbRoles.getValue();
    
    if(nombre.isEmpty() || contrasena.isEmpty()) {
        HistorialLogger.registrarAccion("Login", "Intento de inicio de sesión con campos vacios");
        mostrarAlerta("Error", "Campos vacios", "Por favor ingrese nombre y contraseña", AlertType.ERROR);
        return;
    }
    
    if(autenticarUsuarioDesdeBD(nombre, contrasena, rolSeleccionado)) {
        String rol = obtenerRol(nombre);
        
        HistorialLogger.registrarAccion("Login", 
            "Usuario " + nombre + " (" + rol + ") inició sesión correctamente");
        
        mostrarAlerta("Éxito", "Credenciales correctas", 
                    "Bienvenido a Sistema Ticket, " + nombre + "! Rol: " + rol, AlertType.INFORMATION);
        
        try {
            String fxmlFile = rol.equals("Admin") ? "Configurar.fxml" : "MenuOperaciones.fxml";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(rol.equals("Admin") ? "Configuración" : "Menu de Operaciones");
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Login", 
                "Error al cargar pantalla: " + e.getMessage());
            mostrarAlerta("Error", "Error al cargar pantalla", 
                        e.getMessage(), AlertType.ERROR);
        }
    } else {
        HistorialLogger.registrarAccion("Login", 
            "Intento fallido de inicio de sesión para: " + nombre);
        mostrarAlerta("Error", "Credenciales inválidas", 
                    "Nombre, contraseña o rol incorrectos", AlertType.ERROR);
    }
}
    private boolean autenticarUsuario(String usuario, String contrasena, String rolSeleccionado) {
        return USUARIOS.containsKey(usuario) && 
                ROLES.get(usuario).equalsIgnoreCase(rolSeleccionado)&&
               USUARIOS.get(usuario).equals(contrasena)  
               ;
    }
    
    private String obtenerRol(String usuario) {
        return ROLES.getOrDefault(usuario, "Usuario");
    }
    
    private void mostrarAlerta(String titulo, String encabezado, String contenido, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}