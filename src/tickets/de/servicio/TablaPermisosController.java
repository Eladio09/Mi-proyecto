//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;
import tickets.de.servicio.HistorialLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author rolbi
 */

public class TablaPermisosController implements Initializable {

    @FXML private TextField txtNombrePermiso;
    @FXML private TextField txtDescripcionPermiso;
    @FXML private Button btnRegresar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private ComboBox<Permiso> cbPermisosCreados;

    
    public class Permiso {
        private String nombre;
        private String descripcion;

        public Permiso(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
        public String getNombre() {
            return nombre;
        }
        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
  
    private ObservableList<Permiso> permisosList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Permisos", "Pantalla de gestión de permisos inicializada");
        
        cbPermisosCreados.setConverter(new StringConverter<Permiso>() {
            @Override
            public String toString(Permiso permiso) {
                if (permiso == null) {
                    return "";
                }
                return permiso.getNombre() + " - " + permiso.getDescripcion();
            }

            @Override
            public Permiso fromString(String string) {
                return null;
            }
        });

        cargarPermisosDesdeArchivo();
        
        if (permisosList.isEmpty()) {
            cargarDatosEjemplo();
            guardarPermisosEnArchivo();
        }
        
        cbPermisosCreados.setItems(permisosList);
        
        cbPermisosCreados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNombrePermiso.setText(newVal.getNombre());
                txtDescripcionPermiso.setText(newVal.getDescripcion());
            }
        });
    }

     @FXML
    private void handleRegresarAction(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Permisos", "Regresando a pantalla de roles y permisos");
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("RolesPermisos.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar a roles y permisos: " + e.getMessage());
            showAlert("Error", "No se pudo regresar a la pantalla anterior", AlertType.ERROR);
        }
    }

    @FXML
    private void handleEliminarAction(ActionEvent event) {
        Permiso selectedPermiso = cbPermisosCreados.getSelectionModel().getSelectedItem();
        if (selectedPermiso != null) {
            if (showConfirmation("Eliminar Permiso", 
                "¿Está seguro que desea eliminar el permiso: " + selectedPermiso.getNombre() + "?")) {
                
                HistorialLogger.registrarAccion("Permisos", 
                    "Permiso eliminado: " + selectedPermiso.getNombre() + " - " + selectedPermiso.getDescripcion());
                
                permisosList.remove(selectedPermiso);
                guardarPermisosEnArchivo();
                showAlert("Éxito", "Permiso eliminado correctamente: " + selectedPermiso.getNombre(), AlertType.INFORMATION);
                limpiarCampos();
            } else {
                HistorialLogger.registrarAccion("Permisos", "Se ha cancelado la eliminación del permiso");
            }
        } else {
            HistorialLogger.registrarAccion("Permisos", "Intento de eliminar sin selección");
            showAlert("Advertencia", "Seleccione un permiso para eliminar", AlertType.WARNING);
        }
    }

    @FXML
    private void handleEditarAction(ActionEvent event) {
        Permiso selectedPermiso = cbPermisosCreados.getSelectionModel().getSelectedItem();
        if (selectedPermiso != null) {
            HistorialLogger.registrarAccion("Permisos", 
                "Editando permiso: " + selectedPermiso.getNombre() + " - " + selectedPermiso.getDescripcion());
            
            txtNombrePermiso.setText(selectedPermiso.getNombre());
            txtDescripcionPermiso.setText(selectedPermiso.getDescripcion());
            btnGuardar.setText("Actualizar");
            showAlert("Edición", "Puede modificar los datos del permiso seleccionado", AlertType.INFORMATION);
        } else {
            HistorialLogger.registrarAccion("Permisos", "Intento de editar sin selección");
            showAlert("Advertencia", "Por favor seleccione un permiso para editar", AlertType.WARNING);
        }
    }
    
     @FXML
    private void handleGuardarAction(ActionEvent event) {
        String nombrePermiso = txtNombrePermiso.getText().trim();
        String descripcionPermiso = txtDescripcionPermiso.getText().trim();

        if (nombrePermiso.isEmpty()) {
            HistorialLogger.registrarAccion("Permisos", "Validación fallida: Nombre de permiso vacío");
            showAlert("Error", "El nombre del permiso no puede estar vacío", AlertType.ERROR);
            return;
        }

        Permiso nuevoPermiso = new Permiso(nombrePermiso, descripcionPermiso);
        
        if (btnGuardar.getText().equals("Actualizar")) {
            Permiso permisoSeleccionado = cbPermisosCreados.getSelectionModel().getSelectedItem();
            if (permisoSeleccionado != null) {
                HistorialLogger.registrarAccion("Permisos", 
                    "Actualizando permiso: " + permisoSeleccionado.getNombre() + " -> " + nombrePermiso);
                
                int index = permisosList.indexOf(permisoSeleccionado);
                permisosList.set(index, nuevoPermiso);
                showAlert("Éxito", "Permiso actualizado correctamente", AlertType.INFORMATION);
            }
        } else {
            if (permisosList.stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombrePermiso))) {
                HistorialLogger.registrarAccion("Permisos", 
                    "Intento de crear permiso duplicado: " + nombrePermiso);
                showAlert("Error", "El permiso ya existe", AlertType.ERROR);
                return;
            }
            
            HistorialLogger.registrarAccion("Permisos", 
                "Nuevo permiso creado: " + nombrePermiso + " - " + descripcionPermiso);
            
            permisosList.add(nuevoPermiso);
            showAlert("Éxito", "Permiso guardado correctamente", AlertType.INFORMATION);
        }

        guardarPermisosEnArchivo();
        limpiarCampos();
        btnGuardar.setText("Guardar");
    }
    
        private void guardarPermisosEnArchivo() {
        try (FileWriter writer = new FileWriter("Permiso.txt")) {
            for (Permiso permiso : permisosList) {
                writer.write(permiso.getNombre() + "|" + permiso.getDescripcion() + "\n");
            }
            HistorialLogger.registrarAccion("Permisos", "Permisos guardados");
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al guardar permisos: " + e.getMessage());
            System.err.println("Error al guardar permisos: " + e.getMessage());
        }
    }
    
    private void cargarPermisosDesdeArchivo() {
        try {
            File archivo = new File("Permiso.txt");
            if (!archivo.exists()) {
                return;
            }
            
            List<String> lineas = Files.readAllLines(Paths.get("Permiso.txt"));
            for (String linea : lineas) {
                if (!linea.trim().isEmpty() && linea.contains("|")) {
                    String[] partes = linea.split("\\|");
                    if (partes.length >= 2) {
                        permisosList.add(new Permiso(partes[0].trim(), partes[1].trim()));
                    }
                }
            }
            HistorialLogger.registrarAccion("Permisos", "Permisos cargados");
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al cargar permisos: " + e.getMessage());
            System.err.println("Error al cargar permisos: " + e.getMessage());
        }
    }



   

    private void limpiarCampos() {
        txtNombrePermiso.clear();
        txtDescripcionPermiso.clear();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == javafx.scene.control.ButtonType.OK;
    }

    private void cargarDatosEjemplo() {
        permisosList.addAll(
            new Permiso("Crear", "Permite crear nuevos registros"),
            new Permiso("Leer", "Permite visualizar información"),
            new Permiso("Actualizar", "Permite modificar registros existentes"),
            new Permiso("Eliminar", "Permite borrar registros"),
            new Permiso("Exportar", "Permite exportar datos a diferentes formatos")
        );
        
        HistorialLogger.registrarAccion("Permisos", "Datos de ejemplo cargados");
    }
}