/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tickets.de.servicio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SolisUsuariosController implements Initializable {

    @FXML private Button btnRegresar;
    @FXML private Button btnGuardar;
    @FXML private TextField txtFiltrar;
    @FXML private TextArea txtResultados;
    @FXML private ComboBox<String> cbEstados;
    @FXML private TextArea txtNotas;
    
    private List<String> estados = new ArrayList<>();
    private List<String> datosOriginales = new ArrayList<>();

    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListaSolicitudes.fxml"));
            Parent root = loader.load();

            Scene scene = btnRegresar.getScene();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void filtrarDatos() {
        String filtro = txtFiltrar.getText().toLowerCase();
        txtResultados.clear();
        
        if (filtro.isEmpty()) {
           
            for (String dato : datosOriginales) {
                txtResultados.appendText(dato + "\n");
            }
        } else {
            
            for (String dato : datosOriginales) {
                if (dato.toLowerCase().startsWith(filtro)) {
                    txtResultados.appendText(dato + "\n");
                }
            }
        }
    }
    
    @FXML
    private void agregarNota(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            guardarNota();
        }
    }
    
    private void guardarNota() {
        String nota = txtNotas.getText();
        String estadoSeleccionado = cbEstados.getValue();
        
        if (estadoSeleccionado != null && !nota.isEmpty()) {
            txtResultados.appendText("\nNota agregada para " + estadoSeleccionado + ": " + nota + "\n");
            txtNotas.clear();
        }
    }
    
    @FXML
private void guardarTodosCampos(ActionEvent event) {
    try {
        FileWriter writer = new FileWriter("TicketNotas.txt", true);  
        
        writer.write("\n- NUEVO REGISTRO -\n");
        
        if (cbEstados.getValue() != null) {
            writer.write("Estado: " + cbEstados.getValue() + "\n");
        }
        
        writer.write("Filtro aplicado: " + txtFiltrar.getText() + "\n");
        
        writer.write("Resultados:\n" + txtResultados.getText() + "\n");
        
        if (!txtNotas.getText().isEmpty()) {
            writer.write("Nota actual: " + txtNotas.getText() + "\n");
        }
        
        writer.close();
        
        String estadoSeleccionado = cbEstados.getValue();
        String nota = txtNotas.getText();
        
        if (estadoSeleccionado != null && !nota.isEmpty()) {
            for (int i = 0; i < datosOriginales.size(); i++) {
                if (datosOriginales.get(i).equals(estadoSeleccionado)) {
                    datosOriginales.set(i, estadoSeleccionado + " [Nota: " + nota + "]");
                    break;
                }
            }
            
            ObservableList<String> estadosObservable = FXCollections.observableArrayList(datosOriginales);
            cbEstados.setItems(estadosObservable);
            
            txtNotas.clear();
            
            filtrarDatos();
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guardado exitoso");
        alert.setHeaderText(null);
        alert.setContentText("Los datos se han guardado");
        alert.showAndWait();
        
    } catch (IOException e) {
        e.printStackTrace();
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al guardar");
        alert.setHeaderText(null);
        alert.setContentText("Ocurrió un error al guardar los datos.");
        alert.showAndWait();
    }
}
    
    private void cargarEstados() {
        try {
            File archivo = new File("Estados.txt");
            if (!archivo.exists()) {
                System.out.println("El archivo Estados.txt no existe");
                return;
            }
            
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            
            while ((linea = br.readLine()) != null) {
                estados.add(linea);
                datosOriginales.add(linea);
            }
            br.close();
            
            ObservableList<String> estadosObservable = FXCollections.observableArrayList(estados);
            cbEstados.setItems(estadosObservable);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEstados();
        
        txtFiltrar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarDatos();
        });
        
        txtNotas.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !txtNotas.getText().isEmpty()) {
                guardarNota();
            }
        });
    }    
}