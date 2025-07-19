//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.

package tickets.de.servicio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SolisAdminController implements Initializable {
    
    @FXML
    private Button btnRegresar;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ComboBox<String> cbDepartamento;
    @FXML
    private ComboBox<String> cbPrioridad;
    @FXML
    private ComboBox<String> cbLista;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnGuardar;

    private static final String ARCHIVO_ORIGINAL = "NuevoTicket.txt";
    private static final String ARCHIVO_ACTUALIZADO = "ActTicket.txt";
    private static final String ARCHIVO_DEPARTAMENTOS = "Departamentos.txt";
    
    private List<String> lineasTicketActual = new ArrayList<>();
    private String tituloOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDepartamentos();
        
        cbPrioridad.getItems().addAll("Alta", "Media", "Baja");
        
        cargarListaTickets();
        
        txtTitulo.setEditable(false);
        txtDescripcion.setEditable(false);
        cbDepartamento.setDisable(true);
        cbPrioridad.setDisable(true);
    }
    
    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Scene scene = btnRegresar.getScene();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo regresar al menú anterior");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void eventCancelar(ActionEvent event) {
        txtTitulo.clear();
        txtDescripcion.clear();
        cbDepartamento.getSelectionModel().clearSelection();
        cbPrioridad.getSelectionModel().clearSelection();
        cbLista.getSelectionModel().clearSelection();
        lineasTicketActual.clear();
        tituloOriginal = null;
        
        txtTitulo.setEditable(false);
        txtDescripcion.setEditable(false);
        cbDepartamento.setDisable(true);
        cbPrioridad.setDisable(true);
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        if (txtTitulo.getText().isEmpty() || 
            txtDescripcion.getText().isEmpty() || 
            cbDepartamento.getValue() == null || 
            cbPrioridad.getValue() == null) {
            
            mostrarAlerta("Error", "Todos los campos son obligatorios");
            return;
        }
        
        if (tituloOriginal != null) {
            guardarTicketActualizado();
        } else {
            guardarNuevoTicket();
        }
        
        eventCancelar(event);
    }
    
    
    private void cargarDepartamentos() {
        Set<String> departamentosUnicos = new HashSet<>();
        File archivo = new File(ARCHIVO_DEPARTAMENTOS);
        
        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split("\\|");
                    if (partes.length >= 2) {
                        departamentosUnicos.add(partes[1].trim());
                    }
                }
                
                cbDepartamento.getItems().addAll(departamentosUnicos);
                
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo leer el archivo de departamentos");
                e.printStackTrace();
                
                cbDepartamento.getItems().addAll("TI", "Ventas");
            }
        } else {
            mostrarAlerta("Información", "No existe el archivo de departamentos");
            
            cbDepartamento.getItems().addAll("TI", "Ventas", "Soporte", "Recursos Humanos");
        }
    }
    
    private void cargarListaTickets() {
        cbLista.getItems().clear();
        File archivo = new File(ARCHIVO_ORIGINAL);
        
        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.startsWith("Título:")) {
                        String titulo = linea.substring(7).trim();
                        cbLista.getItems().add(titulo);
                    }
                }
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo leer el archivo");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta("Información", "No existe el archivo");
        }
    }
    
    @FXML
    private void cargarTicketSeleccionado(ActionEvent event) {
        String tituloSeleccionado = cbLista.getValue();
        if (tituloSeleccionado != null && !tituloSeleccionado.isEmpty()) {
            cargarDatosTicket(tituloSeleccionado);
        }
    }
    
    private void cargarDatosTicket(String titulo) {
        lineasTicketActual.clear();
        tituloOriginal = titulo;
        boolean ticketEncontrado = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ORIGINAL))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Título:") && linea.substring(7).trim().equals(titulo)) {
                    ticketEncontrado = true;
                    lineasTicketActual.add(linea);
                    txtTitulo.setText(linea.substring(7).trim());
                    
                    for (int i = 0; i < 3; i++) {
                        linea = br.readLine();
                        if (linea != null) {
                            lineasTicketActual.add(linea);
                            if (linea.startsWith("Descripción:")) {
                                txtDescripcion.setText(linea.substring(12).trim());
                            } else if (linea.startsWith("Departamento:")) {
                                cbDepartamento.setValue(linea.substring(13).trim());
                            } else if (linea.startsWith("Prioridad:")) {
                                cbPrioridad.setValue(linea.substring(10).trim());
                            }
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo leer el ticket seleccionado");
            e.printStackTrace();
        }
        
        if (!ticketEncontrado) {
            mostrarAlerta("Error", "No se encontró el ticket seleccionado");
        }
    }
    
    @FXML
    private void eventEditar(ActionEvent event) {
        String tituloSeleccionado = cbLista.getValue();
        if (tituloSeleccionado == null || tituloSeleccionado.isEmpty()) {
            mostrarAlerta("Error", "Seleccione un ticket para editar");
            return;
        }
        
        txtTitulo.setEditable(true);
        txtDescripcion.setEditable(true);
        cbDepartamento.setDisable(false);
        cbPrioridad.setDisable(false);
        
        mostrarAlerta("Edición", "Puede modificar los campos. Los cambios se guardarán");
    }
    
    
    private void guardarTicketActualizado() {
        try {
            String contenido = "Título: " + txtTitulo.getText() +
                             "\nDescripción: " + txtDescripcion.getText() +
                             "\nDepartamento: " + cbDepartamento.getValue() +
                             "\nPrioridad: " + cbPrioridad.getValue() + "\n\n";
            
            File archivo = new File(ARCHIVO_ACTUALIZADO);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
                bw.write(contenido);
            }
            
            mostrarAlerta("Éxito", "Ticket actualizado guardado en " + ARCHIVO_ACTUALIZADO);
            
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar el ticket actualizado");
            e.printStackTrace();
        }
    }
    
    private void guardarNuevoTicket() {
        try {
            String contenido = "Título: " + txtTitulo.getText() +
                             "\nDescripción: " + txtDescripcion.getText() +
                             "\nDepartamento: " + cbDepartamento.getValue() +
                             "\nPrioridad: " + cbPrioridad.getValue() + "\n\n";
            
            File archivo = new File(ARCHIVO_ORIGINAL);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
                bw.write(contenido);
            }
            
            mostrarAlerta("Éxito", "Nuevo ticket guardado en " + ARCHIVO_ORIGINAL);
            
            cargarListaTickets();
            
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar el nuevo ticket");
            e.printStackTrace();
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
