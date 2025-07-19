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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import tickets.de.servicio.HistorialLogger;
/**
 *
 * @author rolbi
 */


public class FlujoTrabajoController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private Button btnGuardar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbxEstados;
    @FXML private ComboBox<String> cbxFlujosExistentes;
    @FXML private ComboBox<String> cbxTransicionesPermitidas;
    @FXML private ComboBox<String> cbxReglasTransicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Flujos Trabajo", "Pantalla de flujos de trabajo inicializada");
        
        
        cbxEstados.getItems().addAll("Nuevo", "En Progreso", "Resuelto", "Cerrado");
        cbxEstados.setEditable(true);
        
        
        cbxFlujosExistentes.getItems().addAll(
            "Soporte Tecnico - Estado: Nuevo [Transicion: Nuevo - En Progreso, Regla: Requiere aprobaciowen]",
            "Ventas - Estado: En Progreso [Transicion: En Progreso - Cerrado, Regla: Sin restricciones]"
        );
        
        
        cbxTransicionesPermitidas.getItems().addAll(
            "Nuevo - En Progreso",
            "En Progreso - Resuelto",
            "Resuelto - Cerrado",
            "En Progreso - Cerrado"
        );
        cbxTransicionesPermitidas.setEditable(true);
        
        
        cbxReglasTransicion.getItems().addAll(
            "Sin restricciones",
            "Requiere aprobacion",
            "Solo administradores",
            "Comentario obligatorio",
            "Tiempo minimo en estado"
        );
        cbxReglasTransicion.setEditable(true);
        cbxReglasTransicion.setDisable(true);
    }
    
    @FXML
    private void eventEliminar(ActionEvent event) {
        String flujoSeleccionado = cbxFlujosExistentes.getValue();
        
        if (flujoSeleccionado == null || flujoSeleccionado.isEmpty()) {
            HistorialLogger.registrarAccion("Flujos Trabajo", "Intento de eliminar sin seleccion");
            mostrarAlerta(AlertType.WARNING, "Seleccion requerida", 
                "No se ha seleccionado un flujo", 
                "Por favor, seleccione un flujo de trabajo para eliminar.");
            return;
        }
        
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText("¿Esta seguro de eliminar este flujo?");
        confirmacion.setContentText("Flujo para eliminar: " + flujoSeleccionado);
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            cbxFlujosExistentes.getItems().remove(flujoSeleccionado);
            actualizarArchivoFlujos();
            HistorialLogger.registrarAccion("Flujos Trabajo", "Flujo eliminado: " + flujoSeleccionado);
            mostrarAlerta(AlertType.INFORMATION, "Eliminacion exitosa", 
                "Flujo eliminado", 
                "El flujo de trabajo se ha eliminado correctamente.");
        } else {
            HistorialLogger.registrarAccion("Flujos Trabajo", "La eliminación del flujo ha sido cancelada");
        }
    }
    
    
      private void limpiarCampos() {
        txtNombre.clear();
        cbxEstados.setValue(null);
        cbxTransicionesPermitidas.setValue(null);
        cbxReglasTransicion.setValue(null);
        cbxReglasTransicion.setDisable(true);
    }
    
    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Flujos Trabajo", "Regresando al menu de operaciones");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar al menu: " + e.getMessage());
            mostrarAlerta(AlertType.ERROR, "Error", 
                "No se pudo cargar la pantalla", 
                "Ocurrio un error al intentar regresar: " + e.getMessage());
        }
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String estadoSeleccionado = cbxEstados.getValue();
        String transicion = cbxTransicionesPermitidas.getValue();
        String regla = cbxReglasTransicion.getValue();
        
        if (nombre.isEmpty() || estadoSeleccionado == null || estadoSeleccionado.isEmpty()) {
            HistorialLogger.registrarAccion("Flujos Trabajo", "Validacion fallida: Campos incompletos");
            mostrarAlerta(AlertType.WARNING, "Campos incompletos", 
                "Faltan datos requeridos para guardar", 
                "Complete el nombre y seleccione un estado.");
            return;
        }
        
        StringBuilder flujoBuilder = new StringBuilder();
        flujoBuilder.append(nombre).append(" - Estado: ").append(estadoSeleccionado);
        
        if (transicion != null && !transicion.isEmpty()) {
            flujoBuilder.append(" [Transición: ").append(transicion);
            
            if (regla != null && !regla.isEmpty()) {
                flujoBuilder.append(", Regla: ").append(regla);
            }
            flujoBuilder.append("]");
        }
        
        String flujoCompleto = flujoBuilder.toString();
        
        if (cbxFlujosExistentes.getItems().contains(flujoCompleto)) {
            HistorialLogger.registrarAccion("Flujos Trabajo", "El flujo ya esta duplicado: " + flujoCompleto);
            mostrarAlerta(AlertType.WARNING, "Flujo existente", 
                "El flujo de trabajo ya existe", 
                "Por favor, modifique los campos para crear un flujo unico.");
            return;
        }
        
        if (!cbxEstados.getItems().contains(estadoSeleccionado)) {
            cbxEstados.getItems().add(estadoSeleccionado);
        }
        
        if (transicion != null && !transicion.isEmpty() && 
            !cbxTransicionesPermitidas.getItems().contains(transicion)) {
            cbxTransicionesPermitidas.getItems().add(transicion);
        }
        
        if (regla != null && !regla.isEmpty() && 
            !cbxReglasTransicion.getItems().contains(regla)) {
            cbxReglasTransicion.getItems().add(regla);
        }
        
        cbxFlujosExistentes.getItems().add(flujoCompleto);
        guardarFlujoEnArchivo(flujoCompleto);
        limpiarCampos();
        
        HistorialLogger.registrarAccion("Flujos Trabajo", "Flujo guardado: " + flujoCompleto);
        mostrarAlerta(AlertType.INFORMATION, "Guardado exitoso", 
            "Flujo de trabajo guardado", 
            "El flujo de trabajo se ha guardado correctamente");
    }
    
    private void guardarFlujoEnArchivo(String flujoCompleto) {
        try {
            File archivo = new File("FlujosTrabajo.txt");
            
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter(archivo, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                out.println(flujoCompleto);
                
            } catch (IOException e) {
                HistorialLogger.registrarAccion("Error", "Error al escribir en archivo: " + e.getMessage());
                mostrarAlerta(AlertType.ERROR, "Error", 
                    "Error al guardar en archivo", 
                    "No se pudo guardar el flujo en el archivo: " + e.getMessage());
            }
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al crear archivo: " + e.getMessage());
            mostrarAlerta(AlertType.ERROR, "Error", 
                "Error al crear archivo", 
                "No se pudo crear el archivo FlujosTrabajo.txt: " + e.getMessage());
        }
    }
    
    @FXML
    private void eventEditar(ActionEvent event) {
        String flujoSeleccionado = cbxFlujosExistentes.getValue();
        
        if (flujoSeleccionado == null || flujoSeleccionado.isEmpty()) {
            HistorialLogger.registrarAccion("Flujos Trabajo", "Intento de editar sin seleccion");
            mostrarAlerta(AlertType.WARNING, "Selección requerida", 
                "No se ha seleccionado un flujo", 
                "Seleccione un flujo de trabajo para editar.");
            return;
        }
        
        String nombre = "";
        String estado = "";
        String transicion = "";
        String regla = "";
        
        int estadoIndex = flujoSeleccionado.indexOf(" - Estado: ");
        if (estadoIndex > 0) {
            nombre = flujoSeleccionado.substring(0, estadoIndex);
            txtNombre.setText(nombre);
            
            int transicionIndex = flujoSeleccionado.indexOf(" [Transición: ");
            if (transicionIndex > 0) {
                estado = flujoSeleccionado.substring(estadoIndex + 11, transicionIndex);
                
                int reglaIndex = flujoSeleccionado.indexOf(", Regla: ");
                if (reglaIndex > 0) {
                    transicion = flujoSeleccionado.substring(transicionIndex + 14, reglaIndex);
                    regla = flujoSeleccionado.substring(reglaIndex + 9, flujoSeleccionado.length() - 1);
                } else {
                    transicion = flujoSeleccionado.substring(transicionIndex + 14, flujoSeleccionado.length() - 1);
                }
            } else {
                estado = flujoSeleccionado.substring(estadoIndex + 11);
            }
        }
        
        cbxEstados.setValue(estado);
        cbxTransicionesPermitidas.setValue(transicion);
        cbxReglasTransicion.setValue(regla);
        cbxFlujosExistentes.getItems().remove(flujoSeleccionado);
        
        HistorialLogger.registrarAccion("Flujos Trabajo", "Editando flujo: " + flujoSeleccionado);
        mostrarAlerta(AlertType.INFORMATION, "Edicion iniciada", 
            "Flujo listo para editar", 
            "Modifique los campos necesarios y guarde los cambios.");
    }
    
    
    private void actualizarArchivoFlujos() {
        File archivo = new File("FlujosTrabajo.txt");
        try (FileWriter fw = new FileWriter(archivo);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            
            for (String flujo : cbxFlujosExistentes.getItems()) {
                out.println(flujo);
            }
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al actualizar archivo: " + e.getMessage());
            mostrarAlerta(AlertType.ERROR, "Error",
                    "Error al actualizar archivo",
                    "No se pudo actualizar el archivo FlujosTrabajo.txt: " + e.getMessage());
        }
    }
    
    @FXML
    private void eventSeleccionTransicion(ActionEvent event) {
        String transicionSeleccionada = cbxTransicionesPermitidas.getValue();
        if (transicionSeleccionada != null && !transicionSeleccionada.isEmpty()) {
            HistorialLogger.registrarAccion("Flujos Trabajo", 
                "Transicion seleccionada: " + transicionSeleccionada);
            
            cbxReglasTransicion.setDisable(false);
            
            if (transicionSeleccionada.contains("Cerrado")) {
                cbxReglasTransicion.setValue("Requiere aprobacion");
            } else {
                cbxReglasTransicion.setValue("Sin restricciones");
            }
        } else {
            cbxReglasTransicion.setDisable(true);
        }
    }
    
    @FXML
    private void eventSeleccionRegla(ActionEvent event) {
        String reglaSeleccionada = cbxReglasTransicion.getValue();
        if (reglaSeleccionada != null && !reglaSeleccionada.isEmpty()) {
            HistorialLogger.registrarAccion("Flujos Trabajo", 
                "Regla de transición seleccionada: " + reglaSeleccionada);
        }
    }
    
  
    
    private void mostrarAlerta(AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}