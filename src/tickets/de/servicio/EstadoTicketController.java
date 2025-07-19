//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
/**
 *
 * @author rolbi
 */


public class EstadoTicketController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private TextField txtNombreEstado;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cbEstadoFinal;
    @FXML private ComboBox<String> cbEstadosPermitidos;
    @FXML private ComboBox<String> cbEstadosExistentes;
    @FXML private Button btnLimpiar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private List<String> estadosExistentes = new ArrayList<>();
    private String estadoSeleccionadoOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Estados Ticket", "Pantalla de estados de ticket inicializada");
        
       
        cbEstadoFinal.getItems().addAll("Si", "No");
        cbEstadosPermitidos.getItems().addAll(
            "Abierto", "En progreso", "En revision", "Resuelta", "Cerrado"
        );
        
        
        cargarEstadosDesdeArchivo();
        
        
        if (cbEstadosExistentes.getItems().isEmpty()) {
            cbEstadosExistentes.getItems().addAll(
                "Abierto - Ticket recien creado (Final: No) [Permite: En progreso, Cerrado]",
                "En progreso - Ticket siendo atendido (Final: No) [Permite: En revision, Cerrado]",
                "Cerrado - Ticket finalizado (Final: Si) [Permite: ]"
            );
            guardarEstadosEnArchivo(); 
        }
        
        cbEstadosExistentes.setEditable(true);
    }
    
     @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Estados Ticket", "Regresando al menu de operaciones");
            Parent root = FXMLLoader.load(getClass().getResource("MenuOperaciones.fxml"));
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar al menu: " + e.getMessage());
            mostrarAlerta("Error", "Error al regresar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eventLimpiar(ActionEvent event) {
        HistorialLogger.registrarAccion("Estados Ticket", "Formulario de estados limpiado");
        limpiarCampos();
        btnGuardar.setText("Guardar");
        btnEditar.setDisable(false);
    }

    private void limpiarCampos() {
        txtNombreEstado.clear();
        txtDescripcion.clear();
        cbEstadoFinal.getSelectionModel().clearSelection();
        cbEstadosPermitidos.getSelectionModel().clearSelection();
    }

    @FXML
    private void eventEditar(ActionEvent event) {
        String estadoSeleccionado = cbEstadosExistentes.getSelectionModel().getSelectedItem();
        
        if (estadoSeleccionado == null || estadoSeleccionado.isEmpty()) {
            HistorialLogger.registrarAccion("Estados Ticket", "No se ha seleccionada nada para editar");
            mostrarAlerta("Error", "Selección requerida", 
                         "Por favor seleccione un estado para editar.", 
                         Alert.AlertType.WARNING);
            return;
        }
        
        HistorialLogger.registrarAccion("Estados Ticket", "Editando estado: " + estadoSeleccionado);
        estadoSeleccionadoOriginal = estadoSeleccionado;
        cargarEstadoEnCampos(estadoSeleccionado);
        btnGuardar.setText("Actualizar");
        btnEditar.setDisable(true);
    }

    @FXML
    private void eventEliminar(ActionEvent event) {
        String estadoSeleccionado = cbEstadosExistentes.getSelectionModel().getSelectedItem();
        
        if (estadoSeleccionado == null || estadoSeleccionado.isEmpty()) {
            HistorialLogger.registrarAccion("Estados Ticket", "Intento de eliminar sin seleccion");
            mostrarAlerta("Error", "Seleccion requerida", 
                        "Por favor seleccione un estado para eliminar.", 
                        Alert.AlertType.WARNING);
            return;
        }
        
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText("¿Está seguro de eliminar este estado?");
        confirmacion.setContentText(estadoSeleccionado);
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            HistorialLogger.registrarAccion("Estados Ticket", "Estado eliminado: " + estadoSeleccionado);
            cbEstadosExistentes.getItems().remove(estadoSeleccionado);
            estadosExistentes.remove(estadoSeleccionado);
            
            
            guardarEstadosEnArchivo();
            
            mostrarAlerta("Hecho", "Estado eliminado", 
                        "El estado se ha eliminado correctamente.", 
                        Alert.AlertType.INFORMATION);
            
            limpiarCampos();
        } else {
            HistorialLogger.registrarAccion("Estados Ticket", "Cancelada eliminacion de estado");
        }
    }

    @FXML
    private void eventGuardar(ActionEvent event) {
        String nombre = txtNombreEstado.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String estadoFinal = cbEstadoFinal.getValue();
        String estadosPermitidos = cbEstadosPermitidos.getValue();

        if (!validarCampos(nombre, descripcion, estadoFinal)) {
            return;
        }

        String nuevoEstado = String.format("%s - %s (Final: %s) [Permite: %s]",
            nombre, descripcion, estadoFinal, 
            estadosPermitidos != null ? estadosPermitidos : "");

        if (btnGuardar.getText().equals("Actualizar")) {
            HistorialLogger.registrarAccion("Estados Ticket", 
                "Actualizando estado: " + estadoSeleccionadoOriginal + " -> " + nuevoEstado);
            actualizarEstado(nuevoEstado);
        } else {
            HistorialLogger.registrarAccion("Estados Ticket", "Creando nuevo estado: " + nuevoEstado);
            crearNuevoEstado(nuevoEstado);
        }
        
       
        guardarEstadosEnArchivo();
    }

    private void guardarEstadosEnArchivo() {
        try {
            
            List<String> estados = new ArrayList<>(cbEstadosExistentes.getItems());
            
            
            File file = new File("Estados.txt");
            try (PrintWriter output = new PrintWriter(file)) {
                for (String estado : estados) {
                    output.println(estado);
                }
            }
            
            HistorialLogger.registrarAccion("Estados Ticket", "Estados guardados en archivo correctamente");
            mostrarAlerta("Hecho", "Se ha guardado", 
                         "Todos los estados han sido guardados", 
                         Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "Error al guardar en archivo: " + e.getMessage());
            mostrarAlerta("Error", "Error al guardar", 
                         "No se pudo guardar en el archivo: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
        }
    }

    private void cargarEstadosDesdeArchivo() {
        try {
            File file = new File("Estados.txt");
            if (file.exists()) {
                List<String> estados = Files.readAllLines(file.toPath());
                cbEstadosExistentes.getItems().clear();
                cbEstadosExistentes.getItems().addAll(estados);
                estadosExistentes.clear();
                estadosExistentes.addAll(estados);
            }
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "Error al cargar estados desde archivo: " + e.getMessage());
            mostrarAlerta("Error", "Error al cargar", 
                         "No se pudo cargar los estados desde el archivo: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
        }
    }
    
    private void cargarEstadoEnCampos(String estado) {
        try {
            String[] partes = estado.split(" - | \\(Final: |\\) \\[Permite: |\\]");
            txtNombreEstado.setText(partes[0]);
            txtDescripcion.setText(partes[1]);
            cbEstadoFinal.setValue(partes[2]);
            
            if (partes.length > 3 && !partes[3].isEmpty()) {
                cbEstadosPermitidos.setValue(partes[3]);
            } else {
                cbEstadosPermitidos.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "Error al cargar estado: " + e.getMessage());
            mostrarAlerta("Error", "Formato inválido", 
                         "No se pudo cargar el estado que selecciono.", 
                         Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos(String nombre, String descripcion, String estadoFinal) {
        StringBuilder errores = new StringBuilder();
        
        if (nombre.isEmpty()) {
            errores.append(" Nombre del estado es requerido\n");
            HistorialLogger.registrarAccion("Estados Ticket", "Validacion fallida: Nombre vacio");
        }
        if (descripcion.isEmpty()) {
            errores.append(" Descripcion es requerida\n");
            HistorialLogger.registrarAccion("Estados Ticket", "Validacion fallida: Descripcion vacia");
        }
        if (estadoFinal == null) {
            errores.append(" Seleccione si es estado final\n");
            HistorialLogger.registrarAccion("Estados Ticket", "Validacion fallida: Estado final no seleccionado");
        }

        if (errores.length() > 0) {
            mostrarAlerta("Validacion", "Campos obligatorios", 
                         errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void crearNuevoEstado(String nuevoEstado) {
        if (cbEstadosExistentes.getItems().contains(nuevoEstado)) {
            HistorialLogger.registrarAccion("Estados Ticket", "Ha intentado duplicar un archivo existente: " + nuevoEstado);
            mostrarAlerta("Advertencia", "Estado duplicado", 
                         "El estado ya esta agregado.", 
                         Alert.AlertType.WARNING);
            return;
        }

        cbEstadosExistentes.getItems().add(nuevoEstado);
        estadosExistentes.add(nuevoEstado);
        cbEstadosExistentes.getSelectionModel().select(nuevoEstado);
        
        mostrarAlerta("Exito", "Estado creado", 
                     "El nuevo estado se ha guardado correctamente.", 
                     Alert.AlertType.INFORMATION);
        
        limpiarCampos();
    }

    private void actualizarEstado(String nuevoEstado) {
        if (!nuevoEstado.equals(estadoSeleccionadoOriginal) && 
            cbEstadosExistentes.getItems().contains(nuevoEstado)) {
            HistorialLogger.registrarAccion("Estados Ticket", "Intento de actualizar a estado duplicado: " + nuevoEstado);
            mostrarAlerta("Advertencia", "Estado duplicado", 
                         "Ya existe un estado con los mismos datos.", 
                         Alert.AlertType.WARNING);
            return;
        }

        cbEstadosExistentes.getItems().remove(estadoSeleccionadoOriginal);
        estadosExistentes.remove(estadoSeleccionadoOriginal);
        
        cbEstadosExistentes.getItems().add(nuevoEstado);
        estadosExistentes.add(nuevoEstado);
        cbEstadosExistentes.getSelectionModel().select(nuevoEstado);
        
        mostrarAlerta("Exito", "Estado actualizado", 
                     "El estado se ha modificado correctamente.", 
                     Alert.AlertType.INFORMATION);
        
        limpiarCampos();
        btnGuardar.setText("Guardar");
        btnEditar.setDisable(false);
    }

    private void mostrarAlerta(String titulo, String encabezado, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}