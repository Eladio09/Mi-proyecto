//Si logras resolverlo, dime como al 37767101.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas :.(
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import tickets.de.servicio.HistorialLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author rolbi
 */


public class DepartamentosController implements Initializable {

    @FXML private TextField txtNombreDep;
    @FXML private TextField txtDescripcionDep;
    @FXML private ComboBox<String> cmbTecnicos;
    @FXML private Button btnLimpiar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;

    private static final String ARCHIVO_DEPARTAMENTOS = "Departamentos.txt";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Departamentos", "Regresando al menu de operaciones");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar al menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void eventLimpiar(ActionEvent event) {
        HistorialLogger.registrarAccion("Departamentos", "Los campos estan limpiados");
        txtNombreDep.clear();
        txtDescripcionDep.clear();
        cmbTecnicos.getSelectionModel().clearSelection();
        mostrarMensaje("Informacion", "Formulario limpiado", 
                      "Todos los campos han sido restablecidos");
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        if (validarCampos()) {
            String nombre = txtNombreDep.getText();
            String descripcion = txtDescripcionDep.getText();
            String tecnico = cmbTecnicos.getValue();
            
            
            guardarDepartamentoEnArchivo(nombre, descripcion, tecnico);
            
            HistorialLogger.registrarAccion("Departamentos", 
                "Guardando departamento: " + nombre + " - Tecnico asignado: " + tecnico);
            
            mostrarMensaje("Exito", "Datos guardados", 
                         "El departamento '" + nombre + "' ha sido guardado correctamente");
            eventLimpiar(event);
        }
    }
    
    private void guardarDepartamentoEnArchivo(String nombre, String descripcion, String tecnico) {
        try {
            
            String linea = String.format("%s|%s|%s|%s\n",
                LocalDateTime.now().format(FORMATO_FECHA),
                nombre,
                descripcion,
                tecnico);
            
            
            Files.write(Paths.get(ARCHIVO_DEPARTAMENTOS), 
                      linea.getBytes(),
                      StandardOpenOption.CREATE,
                      StandardOpenOption.APPEND);
            
            HistorialLogger.registrarAccion("Archivo", 
                "Departamento guardado: " + nombre);
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", 
                "Error al guardar departamento: " + e.getMessage());
            mostrarAlerta("Error", "Error al guardar", 
                        "No se pudo guardar el departamento");
        }
    }
    
    private boolean validarCampos() {
        if (txtNombreDep.getText().isEmpty()) {
            HistorialLogger.registrarAccion("Departamentos", 
                "Validacion fallida: Nombre de departamento vacio");
            mostrarAlerta("Error", "Campo requerido", 
                         "El nombre del departamento es obligatorio");
            return false;
        }
        
        if (cmbTecnicos.getValue() == null) {
            HistorialLogger.registrarAccion("Departamentos", 
                "Validacion fallida: Tecnico no seleccionado");
            mostrarAlerta("Error", "Campo requerido", 
                         "Debe seleccionar un tecnico");
            return false;
        }
        
        return true;
    }
    
    private void mostrarAlerta(String titulo, String header, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarMensaje(String titulo, String header, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private ObservableList<String> cargarTecnicosDesdeArchivo() {
        ObservableList<String> tecnicos = FXCollections.observableArrayList();
        try {
            List<String> lineas = Files.readAllLines(Paths.get("roles.txt"));
            
            for (String linea : lineas) {
                if (linea.trim().isEmpty() || !linea.contains("|")) {
                    continue;
                }
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 1) {
                    String nombreUsuario = partes[0].trim();
                    
                    if (nombreUsuario.toLowerCase().startsWith("tecnico")) {
                        tecnicos.add(nombreUsuario);
                    }
                }
            }
            
            HistorialLogger.registrarAccion("Departamentos", 
                "Tecnicos cargados desde archivo: " + tecnicos.size() + " encontrados");
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", 
                "No se pudo leer el archivo roles.txt: " + e.getMessage());
            
            
            tecnicos.addAll("tecnicoJuan", "tecnicoMaria", "tecnicoCarlos");
        }
        
        return tecnicos;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Departamentos", "Pantalla de departamentos inicializada");
        
        
        ObservableList<String> listaTecnicos = cargarTecnicosDesdeArchivo();
        
        
        cmbTecnicos.setItems(listaTecnicos);
        
        
        if (listaTecnicos.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay tecnicos", 
                        "No se encontraron tecnicos en el archivo roles.txt");
        }
    }
}