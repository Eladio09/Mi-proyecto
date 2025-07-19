/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import tickets.de.servicio.HistorialLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CambiarEstado9Controller implements Initializable {

    @FXML private ComboBox<String> cbNumeroTickets;
    @FXML private ComboBox<String> cbNuevoEstado;
    @FXML private TextArea taComentario;
    @FXML private Button btnEditarEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;

    private List<String> tickets = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTicketsDesdeArchivo();
        inicializarEstados();
        configurarEstadoInicial();
        HistorialLogger.registrarAccion("Estado Ticket", "Pantalla de cambio de estado inicializada");
    }
    
     @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            HistorialLogger.registrarAccion("Navegación", "Regresando al menú de operaciones");
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar MenuOperaciones.fxml: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo regresar al menú: " + e.getMessage());
        }
    }

    @FXML
    private void eventEditarEstado(ActionEvent event) {
        if (cbNumeroTickets.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un ticket primero");
            return;
        }
        
        cbNuevoEstado.setDisable(false);
        taComentario.setDisable(false);
        btnGuardar.setDisable(false);
        
        HistorialLogger.registrarAccion("Estado Ticket", 
            "Iniciando edición para ticket: " + cbNumeroTickets.getValue());
    }

    @FXML
    private void eventGuardar(ActionEvent event) {
        if (validarCampos()) {
            String ticket = cbNumeroTickets.getValue();
            String nuevoEstado = cbNuevoEstado.getValue();
            String comentario = taComentario.getText();
            
            guardarEstadoActualizado(ticket, nuevoEstado, comentario);
            mostrarAlerta("Éxito", "Estado del ticket actualizado correctamente");
            limpiarFormulario();
        }
    }
    
    private void cargarTicketsDesdeArchivo() {
        try {
            File archivo = new File("NuevoTicket.txt");
            if (!archivo.exists()) {
                mostrarAlerta("Información", "No hay tickets registrados");
                return;
            }

            List<String> lineas = Files.readAllLines(Paths.get("NuevoTicket.txt"));
            tickets.clear();
            
            for (String linea : lineas) {
                if (linea.startsWith("Título: ")) {
                    String titulo = linea.substring(8).trim();
                    if (!titulo.isEmpty() && !tickets.contains(titulo)) {
                        tickets.add(titulo);
                    }
                }
            }
            
            cbNumeroTickets.getItems().setAll(tickets);
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al leer archivo de tickets: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la lista de tickets: " + e.getMessage());
        }
    }

    private void inicializarEstados() {
        cbNuevoEstado.getItems().addAll(
            "Pendiente",
            "En progreso",
            "Cerrado"
            
        );
    }

    private void configurarEstadoInicial() {
        cbNuevoEstado.setDisable(true);
        taComentario.setDisable(true);
        btnGuardar.setDisable(true);
    }

    @FXML
    private void eventCambioEstado(ActionEvent event) {
        if (cbNuevoEstado.getValue() != null) {
            HistorialLogger.registrarAccion("Estado Ticket", 
                "Estado preseleccionado: " + cbNuevoEstado.getValue());
        }
    }

    

    private void guardarEstadoActualizado(String ticket, String nuevoEstado, String comentario) {
        try (FileWriter writer = new FileWriter("EstadosActualizados.txt", true);
             PrintWriter printWriter = new PrintWriter(writer)) {
            
            printWriter.println("--- Actualización de Estado ---");
            printWriter.println("Fecha/Hora: " + LocalDateTime.now());
            printWriter.println("Ticket: " + ticket);
            printWriter.println("Estado Anterior: " + obtenerEstadoActual(ticket));
            printWriter.println("Nuevo Estado: " + nuevoEstado);
            printWriter.println("Comentario: " + (comentario.isEmpty() ? "Ninguno" : comentario));
            printWriter.println("------------------------------");
            printWriter.println();
            
            HistorialLogger.registrarAccion("Estado Ticket", 
                "Estado actualizado - Ticket: " + ticket + 
                ", Nuevo Estado: " + nuevoEstado);
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "Error al guardar estado actualizado: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo guardar el cambio de estado: " + e.getMessage());
        }
    }

    private String obtenerEstadoActual(String ticket) {
        return "Desconocido"; 
    }

   

    private boolean validarCampos() {
        if (cbNumeroTickets.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un ticket");
            return false;
        }
        if (cbNuevoEstado.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un nuevo estado");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cbNuevoEstado.setValue(null);
        taComentario.clear();
        configurarEstadoInicial();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}