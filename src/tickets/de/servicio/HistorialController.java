//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

/**
 *
 * @author rolbi
 */


public class HistorialController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private Button btnMostrarHistorial;
    @FXML private Button btnLimpiar;
    @FXML private TextArea txtHistorial;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtHistorial.setEditable(false);
        txtHistorial.setWrapText(true);
    }    
    
    @FXML
    private void handleMostrarHistorial(ActionEvent event) {
        String historialCompleto = HistorialLogger.obtenerHistorialCompleto();
        txtHistorial.setText(historialCompleto);
        txtHistorial.positionCaret(txtHistorial.getLength());
    }
    
    @FXML
    private void handleRegresar(ActionEvent event) {
        try {
            
            Parent root = FXMLLoader.load(getClass().getResource("MenuOperaciones.fxml"));
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLimpiarHistorial(ActionEvent event) {
        txtHistorial.clear();
    }
}