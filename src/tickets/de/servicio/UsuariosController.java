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

import java.net.URL;
import java.util.ResourceBundle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rolbi
 */

public class UsuariosController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private TextField txtNombreCompleto;
    @FXML private ComboBox<String> cbRol;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtUsuario;
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private Button btnLimpiar;
    @FXML private Button btnGuardar;
    @FXML private ComboBox<String> cbLista;
    @FXML private Button btnEditar;
    
    private List<String> usuariosGuardados = new ArrayList<>();
    private static final String ARCHIVO_USUARIOS = "Usuarios.txt";
    
    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al regresar", e.getMessage());
        }
    }
    
    @FXML
    private void eventLimpiar(ActionEvent event) {
        txtNombreCompleto.clear();
        cbRol.getSelectionModel().clearSelection();
        txtCorreo.clear();
        txtContrasena.clear();
        txtUsuario.clear();
        cbDepartamento.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        if (!validarCampos()) return;
        
        String usuarioData = String.format("%s,%s,%s,%s,%s,%s",
            txtNombreCompleto.getText(),
            txtUsuario.getText(),
            txtCorreo.getText(),
            txtContrasena.getText(),
            cbRol.getValue(),
            cbDepartamento.getValue());
        
        usuariosGuardados.add(usuarioData);
        cbLista.getItems().add(txtNombreCompleto.getText() + " (" + txtUsuario.getText() + ")");
        guardarEnArchivo(usuarioData);
        
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario guardado", 
                    "El usuario ha sido guardado correctamente.");
        eventLimpiar(event);
    }
    
     @FXML
    private void eventEditar(ActionEvent event) {
        if (cbLista.getSelectionModel().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ningún usuario ha sido seleccionado", 
                        "Seleccione un usuario para editar.");
            return;
        }
        
        int index = cbLista.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < usuariosGuardados.size()) {
            String[] datos = usuariosGuardados.get(index).split(",");
            
            if (datos.length >= 6) {
                txtNombreCompleto.setText(datos[0]);
                txtUsuario.setText(datos[1]);
                txtCorreo.setText(datos[2]);
                txtContrasena.setText(datos[3]);
                cbRol.setValue(datos[4]);
                cbDepartamento.setValue(datos[5]);
                
                
                usuariosGuardados.remove(index);
                cbLista.getItems().remove(index);
                
                
                guardarTodosUsuarios();
            }
        }
    }
    
    
    private boolean validarCampos() {
        if (txtNombreCompleto.getText().isEmpty() || 
            cbRol.getValue() == null || 
            txtCorreo.getText().isEmpty() || 
            txtContrasena.getText().isEmpty() || 
            txtUsuario.getText().isEmpty() || 
            cbDepartamento.getValue() == null) {
            
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Campos incompletos", 
                        "Por favor omplete todos los campos.");
            return false;
        }
        
        if (!txtCorreo.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Correo inválido", 
                        "Por favor ingrese un correo válido.");
            return false;
        }
        return true;
    }
    
   
    private void guardarEnArchivo(String usuarioData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            writer.write(usuarioData);
            writer.newLine();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar", 
                        "No ha sido posible guardar");
        }
    }
    
    private void guardarTodosUsuarios() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (String usuario : usuariosGuardados) {
                writer.write(usuario);
                writer.newLine();
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar", 
                        "No ha sido posible guardar");
        }
    }
    
    private void cargarUsuariosDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            usuariosGuardados.clear();
            cbLista.getItems().clear();
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    usuariosGuardados.add(linea);
                    String[] datos = linea.split(",");
                    if (datos.length >= 2) {
                        cbLista.getItems().add(datos[0] + " (" + datos[1] + ")");
                    }
                }
            }
        } catch (IOException e) {
            
            System.out.println("Archivo de usuarios no encontrado, se creará uno nuevo.");
        }
    }
    
    private void cargarDepartamentosDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Departamentos.txt"))) {
            cbDepartamento.getItems().clear();
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                String[] columnas = linea.split("\\|");
                if (columnas.length > 1) {  
                    String departamento = columnas[1].trim();  
                    if (!departamento.isEmpty()) {
                        cbDepartamento.getItems().add(departamento);
                    }
                }
            }
        } catch (IOException e) {
            cargarDepartamentosPorDefecto();
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Error al cargar departamentos", 
                        "No se pudo leer Departamentos.txt. Se cargarán ajemplos");
        }
    }
    
    private void cargarDepartamentosPorDefecto() {
        cbDepartamento.getItems().addAll(
            "Ventas", 
            "TI"
        );
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbRol.getItems().addAll("Administrador", "Técnico", "Usuario");
        cargarDepartamentosDesdeArchivo();
        cargarUsuariosDesdeArchivo();
        
        btnLimpiar.setOnAction(this::eventLimpiar);
        btnGuardar.setOnAction(this::eventGuardar);
        btnEditar.setOnAction(this::eventEditar);
    }
}