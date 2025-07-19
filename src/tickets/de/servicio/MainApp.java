//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rolbi
 */

public class MainApp extends Application {
    
    private static MainApp instance;
    private Stage primaryStage;
    private final Map<String, Stage> ventanasAbiertas = new HashMap<>();
    
    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        this.primaryStage = stage;
        
        HistorialLogger.registrarAccion("Sistema", "Aplicación iniciada");
        
        cargarVentana("Login", "/tickets/de/servicio/views/Login.fxml");
        
        primaryStage.setTitle("Sistema de Gestión de Tickets");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icono.png")));
        primaryStage.setOnCloseRequest(e -> {
            HistorialLogger.registrarAccion("Sistema", "Aplicacion finalizada");
            System.exit(0);
        });
    }
    
    public static MainApp getInstance() {
        return instance;
    }
    
    public void cargarVentana(String nombre, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(nombre);
            
            if (ventanasAbiertas.isEmpty()) {
                primaryStage.setScene(stage.getScene());
                primaryStage.show();
            } else {
                stage.show();
            }
            
            ventanasAbiertas.put(nombre, stage);
            HistorialLogger.registrarAccion(nombre, "Ventana abierta");
            
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Sistema", 
                "Error al cargar ventana " + nombre + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public void cerrarVentana(Stage stage) {
        if (stage != null) {
            String nombreVentana = obtenerNombreVentana(stage);
            HistorialLogger.registrarAccion(nombreVentana, "Ventana cerrada");
            stage.close();
            ventanasAbiertas.remove(nombreVentana);
        }
    }
    
   
    public void cerrarVentanaActual(javafx.scene.Node component) {
        Window window = component.getScene().getWindow();
        if (window instanceof Stage) {
            cerrarVentana((Stage) window);
        }
    }
    
    private String obtenerNombreVentana(Stage stage) {
        return ventanasAbiertas.entrySet().stream()
                .filter(entry -> entry.getValue().equals(stage))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Desconocida");
    }
    
    public static void main(String[] args) {
        launch(args);
    }


}