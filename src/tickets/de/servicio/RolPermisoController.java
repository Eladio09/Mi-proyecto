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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * FXML Controller class
 */
/**
 *
 * @author rolbi
 */
public class RolPermisoController implements Initializable {

    @FXML private TextField txtNombreRol;
    @FXML private TextField txtDescripcionRol;
    @FXML private TextField txtContraseña;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnRegresar;
    @FXML private Button btnGuardar;
    @FXML private ComboBox<Rol> cbRolesCreados;

    public class Rol {
        private String nombre;
        private String descripcion;
        private String contraseña;

        public Rol(String nombre, String descripcion, String contraseña) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.contraseña = contraseña;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getContraseña() {
            return contraseña;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private ObservableList<Rol> rolesList = FXCollections.observableArrayList();
    private final String ARCHIVO_ROLES = "roles.txt";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Roles y Permisos", "Pantalla de gestión de roles inicializada");
        
        cbRolesCreados.setConverter(new StringConverter<Rol>() {
            
            @Override
            public String toString(Rol rol) {
                if (rol == null) {
                    return "";
                }
                return rol.getNombre() + " - " + rol.getDescripcion();
            }

            @Override
            public Rol fromString(String string) {
                return null; 
            }
        });

        cargarRolesDesdeArchivo();
        
        cbRolesCreados.setItems(rolesList);
        
        cbRolesCreados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNombreRol.setText(newVal.getNombre());
                txtDescripcionRol.setText(newVal.getDescripcion());
                txtContraseña.setText(newVal.getContraseña());
            }
        });
    }
    
        @FXML
    private void handleGuardarAction(ActionEvent event) {
        String nombreRol = txtNombreRol.getText().trim();
        String descripcionRol = txtDescripcionRol.getText().trim();
        String contraseña = txtContraseña.getText().trim();

        if (nombreRol.isEmpty()) {
            HistorialLogger.registrarAccion("Roles y Permisos", "Validación fallida: Nombre de rol vacío");
            showAlert("Error", "El nombre del rol no puede estar vacio", AlertType.ERROR);
            return;
        }

        if (contraseña.isEmpty()) {
            HistorialLogger.registrarAccion("Roles y Permisos", "Validación fallida: Contraseña vacia");
            showAlert("Error", "La contraseña no puede estar vaciaa", AlertType.ERROR);
            return;
        }

        Rol nuevoRol = new Rol(nombreRol, descripcionRol, contraseña);
        
        if (btnGuardar.getText().equals("Actualizar")) {
            Rol rolSeleccionado = cbRolesCreados.getSelectionModel().getSelectedItem();
            if (rolSeleccionado != null) {
                HistorialLogger.registrarAccion("Roles y Permisos", 
                    "Actualizando el rol: " + rolSeleccionado.getNombre() + " -> " + nombreRol);
                
                int index = rolesList.indexOf(rolSeleccionado);
                rolesList.set(index, nuevoRol);
                guardarRolesEnArchivo();
                showAlert("Exito", "Rol actualizado correctamente", AlertType.INFORMATION);
            }
        } else {
           
            if (rolesList.stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(nombreRol))) {
                HistorialLogger.registrarAccion("Roles y Permisos", 
                    "Intento de crear rol duplicado: " + nombreRol);
                showAlert("Eror", "El rol ya existe", AlertType.ERROR);
                return;
            }
            
            HistorialLogger.registrarAccion("Roles y Permisos", 
                "Nuevo rol creado: " + nombreRol + " - " + descripcionRol);
            
            rolesList.add(nuevoRol);
            guardarRolesEnArchivo();
            showAlert("Exito", "Rol guardado correctamente", AlertType.INFORMATION);
        }

        limpiarCampos();
        btnGuardar.setText("Guardar");
    }

    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            HistorialLogger.registrarAccion("Roles y Permisos", "Regresando a pantalla de roles y permisos");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RolesPermisos.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo regresar a roles y permisos: " + e.getMessage());
            showAlert("Error", "No se pudo regresar a la pantalla anterior", AlertType.ERROR);
        }
    }

       @FXML
    private void handleEliminarAction(ActionEvent event) {
        Rol selectedRole = cbRolesCreados.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            if (showConfirmation("Eliminar Rol", 
                "¿Está seguro de eliminar el rol: " + selectedRole.getNombre() + "?")) {
                
                HistorialLogger.registrarAccion("Roles y Permisos", 
                    "Rol eliminado: " + selectedRole.getNombre() + " - " + selectedRole.getDescripcion());
                
                rolesList.remove(selectedRole);
                guardarRolesEnArchivo();
                showAlert("Exito", "Rol eliminado correctamente: " + selectedRole.getNombre(), AlertType.INFORMATION);
                limpiarCampos();
            } else {
                HistorialLogger.registrarAccion("Roles y Permisos", "La eliminación de rol se ha cancelado");
            }
        } else {
            HistorialLogger.registrarAccion("Roles y Permisos", "Intento de eliminar sin selección");
            showAlert("Advertencia", "Seleccione un rol para eliminar", AlertType.WARNING);
        }
    }
    
    private void cargarRolesDesdeArchivo() {
        File archivo = new File(ARCHIVO_ROLES);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                return;
            } catch (IOException e) {
                HistorialLogger.registrarAccion("Error", "No se pudo crear el archivo de roles: " + e.getMessage());
                showAlert("Error", "No se pudo crear el archivo de roles", AlertType.ERROR);
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ROLES))) {
            rolesList.clear();
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 3) {
                    rolesList.add(new Rol(partes[0], partes[1], partes[2]));
                }
            }
            HistorialLogger.registrarAccion("Roles y Permisos", "Roles cargados desde archivo");
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al leer el archivo de roles: " + e.getMessage());
            showAlert("Error", "Error al leer el archivo de roles", AlertType.ERROR);
        }
    }

    private void guardarRolesEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_ROLES))) {
            for (Rol rol : rolesList) {
                writer.write(rol.getNombre() + "|" + rol.getDescripcion() + "|" + rol.getContraseña());
                writer.newLine();
            }
            HistorialLogger.registrarAccion("Roles y Permisos", "Roles guardados");
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al guardar el rol: " + e.getMessage());
            showAlert("Error", "Error al guardar el rol", AlertType.ERROR);
        }
    }


 

    @FXML
    private void handleEditarAction(ActionEvent event) {
        Rol selectedRole = cbRolesCreados.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            HistorialLogger.registrarAccion("Roles y Permisos", 
                "Editando rol: " + selectedRole.getNombre() + " - " + selectedRole.getDescripcion());
            
            txtNombreRol.setText(selectedRole.getNombre());
            txtDescripcionRol.setText(selectedRole.getDescripcion());
            txtContraseña.setText(selectedRole.getContraseña());
            btnGuardar.setText("Actualizar");
            showAlert("Edición", "Puede modificar los datos del rol seleccionado", AlertType.INFORMATION);
        } else {
            HistorialLogger.registrarAccion("Roles y Permisos", "Intento de editar sin selección");
            showAlert("Advertencia", "Por favor seleccione un rol para editar", AlertType.WARNING);
        }
    }


    private void limpiarCampos() {
        txtNombreRol.clear();
        txtDescripcionRol.clear();
        txtContraseña.clear();
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
}