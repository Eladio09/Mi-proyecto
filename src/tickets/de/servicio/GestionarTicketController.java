//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import tickets.de.servicio.HistorialLogger;

/**
 *
 * @author rolbi
 */
public class GestionarTicketController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private ComboBox<String> cbPrioridad;
    @FXML private ComboBox<String> cbEstados;
    @FXML private Button btnAdjuntos;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Button btnTomaTicket;
    @FXML private Button btnActualizarTicket;
    @FXML private Button btnCerrarTicket;
    
    private File archivoAdjunto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Gestión Tickets", "Pantalla de gestión de tickets inicializada");
        cargarDepartamentosDesdeArchivo();
        
        // Inicialización de ComboBox de Prioridad
        cbPrioridad.getItems().addAll(
            "Baja", 
            "Media", 
            "Alta", 
            "Crítica"
        );
        
        // Inicialización de ComboBox de Estados
        cbEstados.getItems().addAll(
            "En progreso",
            "Cerrado",
            "Pendiente"
        );
        
        // Listener para cambios de estado
        cbEstados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                HistorialLogger.registrarAccion("Estado Ticket", "Estado cambiado a: " + newVal);
            }
        });
    }

    @FXML
    private void eventRegresar(ActionEvent event) {
        HistorialLogger.registrarAccion("Gestion Tickets", "Regresando al menú de operaciones");
        cargarPantalla("MenuOperaciones.fxml", event);
    }
    
    @FXML
    private void eventCancelar(ActionEvent event) {
        HistorialLogger.registrarAccion("Gestion Tickets", "Formulario de ticket limpiado");
        txtTitulo.clear();
        txtDescripcion.clear();
        cbDepartamento.getSelectionModel().clearSelection();
        cbPrioridad.getSelectionModel().clearSelection();
        cbEstados.getSelectionModel().clearSelection();
        archivoAdjunto = null;
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        if (validarCamposObligatorios()) {
            String titulo = txtTitulo.getText();
            String descripcion = txtDescripcion.getText();
            String departamento = cbDepartamento.getValue();
            String prioridad = cbPrioridad.getValue();
            String estado = cbEstados.getValue();
            
            try {
                FileWriter writer = new FileWriter("NuevoTicket.txt", true);
                PrintWriter printWriter = new PrintWriter(writer);
                
                printWriter.println("-Ticket Nuevo-");
                printWriter.println("Fecha/Hora: " + java.time.LocalDateTime.now());
                printWriter.println("Título: " + titulo);
                printWriter.println("Descripción: " + descripcion);
                printWriter.println("Departamento: " + departamento);
                printWriter.println("Prioridad: " + prioridad);
                printWriter.println("Estado: " + estado);
                if (archivoAdjunto != null) {
                    printWriter.println("Archivo adjunto: " + archivoAdjunto.getName());
                }
                printWriter.println("---------");
                printWriter.println();
                
                printWriter.close();
                
                HistorialLogger.registrarAccion("Gestion Tickets", 
                    "Nuevo ticket guardado - Título: " + titulo + 
                    ", Departamento: " + departamento + 
                    ", Prioridad: " + prioridad +
                    ", Estado: " + estado);
                
                mostrarAlerta("Éxito", "Ticket guardado correctamente");
                eventCancelar(event);
                
            } catch (IOException e) {
                HistorialLogger.registrarAccion("Error", "No se pudo guardar el ticket: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo guardar el ticket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void eventCambioEstado(ActionEvent event) {
        String estadoSeleccionado = cbEstados.getValue();
        if (estadoSeleccionado != null) {
            HistorialLogger.registrarAccion("Estado Ticket", 
                "Estado modificado manualmente a: " + estadoSeleccionado);
        }
    }
    
    private void cargarDepartamentosDesdeArchivo() {
        try {
            List<String> lineas = Files.readAllLines(Paths.get("Departamentos.txt"));
                    
            List<String> departamentos = lineas.stream()
                .map(linea -> {
                    String[] partes = linea.split("\\|");
                    return partes.length > 1 ? partes[1].trim() : "";
                })
                .filter(departamento -> !departamento.isEmpty())
                .distinct() 
                .sorted() 
                .collect(Collectors.toList());
            
            cbDepartamento.getItems().addAll(departamentos);
            
            HistorialLogger.registrarAccion("Gestion Tickets", 
                "Departamentos cargados desde archivo: " + departamentos.size() + " registros");
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar el archivo de departamentos: " + e.getMessage());
                       
            cbDepartamento.getItems().addAll(
                "TI", 
                "Ventas", 
                "Operaciones"
            );
            
            mostrarAlerta("Advertencia", "No se pudo cargar el archivo de departamentos. Se usan valores por defecto.");
        }
    }
    
    @FXML
    private void eventTomaTicket(ActionEvent event) {
        HistorialLogger.registrarAccion("Gestion Tickets", "Iniciando toma de ticket");
        cargarPantalla("TomaActualizaCierraTicket.fxml", event);
    }
    
    @FXML
    private void eventActualizarTicket(ActionEvent event) {
        HistorialLogger.registrarAccion("Gestion Tickets", "Iniciando actualización de ticket");
        cargarPantalla("TomaActualizaCierraTicket.fxml", event);
    }
    
    @FXML
    private void eventCerrarTicket(ActionEvent event) {
        HistorialLogger.registrarAccion("Gestion Tickets", "Iniciando cierre de ticket");
        cargarPantalla("TomaActualizaCierraTicket.fxml", event);
    }
    
    @FXML
    private void eventAdjuntos(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo adjunto");
        archivoAdjunto = fileChooser.showOpenDialog(btnAdjuntos.getScene().getWindow());
        if (archivoAdjunto != null) {
            HistorialLogger.registrarAccion("Gestion Tickets", 
                "Archivo adjunto seleccionado: " + archivoAdjunto.getName());
        }
    }
 
    private boolean validarCamposObligatorios() {
        if (txtTitulo.getText().isEmpty() || txtDescripcion.getText().isEmpty() || 
            cbDepartamento.getSelectionModel().isEmpty() || cbPrioridad.getSelectionModel().isEmpty()) {
            HistorialLogger.registrarAccion("Gestion Tickets", "Validación fallida: Campos obligatorios incompletos");
            mostrarAlerta("Error", "Por favor complete todos los campos obligatorios");
            return false;
        }
        return true;
    }
    
    private void cargarPantalla(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            HistorialLogger.registrarAccion("Navegación", "Cargando pantalla: " + fxml);
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar " + fxml + ": " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la pantalla: " + fxml);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}