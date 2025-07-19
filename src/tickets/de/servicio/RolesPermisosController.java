
//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.

package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import tickets.de.servicio.HistorialLogger;

/**
 * 
 */

/**
 *
 * @author rolbi
 */
public class RolesPermisosController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private Button btnCrearRol;
    @FXML private Button btnCrearPermiso;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Roles y Permisos", "Pantalla principal de roles y permisos inicializada");
    }
    
    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Roles y Permisos", "Regresando al menú de operaciones");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar al menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void eventCrearRol(ActionEvent event) {
        HistorialLogger.registrarAccion("Roles y Permisos", "Creación de roles");
        cargarPantalla("RolPermiso.fxml", event);
    }
    
    
    
    
    @FXML
    private void eventCrearPermiso(ActionEvent event) {
        HistorialLogger.registrarAccion("Roles y Permisos", "Creación de permisos");
        cargarPantalla("TablaPermisos.fxml", event);
    }
    
    private void cargarPantalla(String fxmlFile, ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Navegación", "Cargando pantalla: " + fxmlFile);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Button sourceButton = (Button) event.getSource();
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}