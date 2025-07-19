//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import tickets.de.servicio.HistorialLogger;

/**
 *
 * @author rolbi
 */

public class MenuOperacionesController implements Initializable {

    @FXML private MenuButton menuButton;
    @FXML private MenuItem menuItemRoles;
    @FXML private MenuItem menuItemDepartamentos;
    @FXML private MenuItem menuItemUsuarios;
    @FXML private MenuItem menuItemEstadoTicket;
    @FXML private MenuItem menuItemFlujosTrabajo;
    @FXML private MenuItem menuItemGestionarTicket;
    @FXML private MenuItem menuItemConsultarLista;
    @FXML private MenuItem menuItemCambiarEstado;
    @FXML private MenuItem menuItemAgregarNota;
    @FXML private MenuItem menuItemHistorial;
    @FXML private MenuItem menuItemSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Menú Operaciones", "Menú de operaciones inicializado");
        menuButton.setText("Menú");
    }
    
    @FXML 
    private void handleMenuItemRoles() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Roles y Permisos");
        cambiarPantalla("RolesPermisos.fxml"); 
    } 
    
    @FXML 
    private void handleMenuItemDepartamentos() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Departamentos");
        cambiarPantalla("Departamentos.fxml"); 
    }
    @FXML 
    private void handleMenuItemUsuarios() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Gestión de Usuarios");
        cambiarPantalla("Usuarios.fxml"); 
    }
    @FXML 
    private void handleMenuItemEstadoTicket() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Estados de Ticket");
        cambiarPantalla("EstadoTicket.fxml"); 
    }
    @FXML 
    private void handleMenuItemFlujosTrabajo() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Flujos de Trabajo");
        cambiarPantalla("FlujoTrabajo.fxml"); 
    }
    
    @FXML 
    private void handleMenuItemGestionarTicket() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Gestión de Tickets");
        cambiarPantalla("GestionarTicket.fxml"); 
    }
    @FXML 
    private void handleMenuItemConsultarLista() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Consultar Lista");
        cambiarPantalla("ListaSolicitudes.fxml"); 
    }
    @FXML 
    private void handleMenuItemCambiarEstado() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Cambiar Estado");
        cambiarPantalla("CambiarEstado9.fxml"); 
    }
    @FXML 
    private void handleMenuItemAgregarNota() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Agregar Nota");
        cambiarPantalla("AgregarNota10.fxml"); 
    }
    @FXML 
    private void handleMenuItemSalir() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Usuario salió del sistema");
        cambiarPantalla("Login.fxml"); 
    }
    
    @FXML 
    private void handleMenuItemHistorial(ActionEvent event) {
        HistorialLogger.registrarAccion("Menú Operaciones", "Accediendo al historial del sistema");
        cambiarPantalla("Historial.fxml");
    }

    private void cambiarPantalla(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) menuButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            HistorialLogger.registrarAccion("Navegación", "Cargada pantalla: " + fxmlFile);
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    void setRolUsuario(String rol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void inicializarConRol(String rol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}