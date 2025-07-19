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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

/**
 * 
 *
 * @author rolbi
 */
public class ListaSolicitudesController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML private MenuButton menuBtn;
    @FXML private MenuItem menuItemMisTicket;
    @FXML private MenuItem menuItemPendientes;
    @FXML private MenuItem menuItemGestion;
    
    @FXML
    private void eventRegresar(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();

            Scene scene = btnRegresar.getScene();

            Stage stage = (Stage) btnRegresar.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @FXML 
    private void handleMenuItemMisTicket() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Roles y Permisos");
        cambiarPantalla("SolisUsuarios.fxml"); 
    } 
    
    @FXML 
    private void handleMenuItemPendientes() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Departamentos");
        cambiarPantalla("SolisTec.fxml"); 
    }
    @FXML 
    private void handleMenuItemGestion() { 
        HistorialLogger.registrarAccion("Menú Operaciones", "Seleccionado: Gestión de Usuarios");
        cambiarPantalla("SolisAdmin.fxml"); 
    }
    
    private void cambiarPantalla(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) menuBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            HistorialLogger.registrarAccion("Navegación", "Cargada pantalla: " + fxmlFile);
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
