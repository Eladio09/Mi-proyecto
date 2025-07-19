//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tickets.de.servicio.HistorialLogger;

/**
 *
 * @author rolbi
 */
public class TomaActualizaCierraTicketController implements Initializable {

    @FXML private ComboBox<String> listaTicketsComboBox;
    @FXML private ComboBox<String> estadoTicketComboBox;
    @FXML private TextArea comentariosTextArea;
    @FXML private Button editarButton;
    @FXML private Button guardarButton;
    @FXML private Button regresarButton;

    private Map<String, String> estadosActuales = new HashMap<>();
    private Map<String, String> historialComentarios = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTicketsDesdeArchivo();
        inicializarEstadosTicket();
        configurarComponentes();
    }
    
    @FXML
    private void handleRegresarAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("GestionarTicket.fxml"));
            Stage stage = (Stage) regresarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            HistorialLogger.registrarAccion("Navegacion", "Regresando a gestion de tickets");
        } catch (IOException ex) {
            mostrarMensajeError("Error", "No se pudo cargar la pantalla de gestion");
            HistorialLogger.registrarAccion("Error", "No se pudo regresar a gestion: " + ex.getMessage());
        }
    }
    
    @FXML
    private void handleEditarAction() {
        habilitarEdicion(true);
        mostrarMensaje("Edición habilitada", "Puede modificar el estado del ticket");
    }


    private void cargarTicketsDesdeArchivo() {
        try {
            List<String> lineas = Files.readAllLines(Paths.get("NuevoTicket.txt"));
            List<String> tickets = new ArrayList<>();
            
            String tituloActual = "";
            String fechaActual = "";
            
            for (String linea : lineas) {
                if (linea.startsWith("Fecha/Hora: ")) {
                    fechaActual = linea.substring("Fecha/Hora: ".length());
                } else if (linea.startsWith("Titulo: ")) {
                    tituloActual = linea.substring("Título: ".length());
                    String ticketId = "TICK-" + (tickets.size() + 1) + " - " + tituloActual;
                    tickets.add(ticketId);
                    if (!estadosActuales.containsKey(ticketId)) {
                        estadosActuales.put(ticketId, "Abierto");
                    }
                }
            }
            
            listaTicketsComboBox.getItems().addAll(tickets);
            
            HistorialLogger.registrarAccion("Gestion Ticket", 
                "Tickets cargados: " + tickets.size() + " registro");
            
            if (!listaTicketsComboBox.getItems().isEmpty()) {
                listaTicketsComboBox.getSelectionModel().selectFirst();
                actualizarEstadoActual();
            }
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "No se pudo cargar el archivo: " + e.getMessage());
            
            estadosActuales.put("Ticket-01 - Impresora no funciona", "Abierto");
            estadosActuales.put("Ticket-02 - Error en planta", "En progreso");
            estadosActuales.put("Ticket-03 - Solicitud dia descanso", "Pendiente");
            
            listaTicketsComboBox.getItems().addAll(estadosActuales.keySet());
            
            if (!listaTicketsComboBox.getItems().isEmpty()) {
                listaTicketsComboBox.getSelectionModel().selectFirst();
                actualizarEstadoActual();
            }
            
            mostrarMensaje("Advertencia", "No se pudo cargar el archivo de tickets. Se cargaron datos de ejemplo.");
        }
    }

    private void inicializarEstadosTicket() {
        estadoTicketComboBox.getItems().addAll(
            "Abierto",
            "En progreso",
            "Pendiente",
            "Resuelto",
            "Cerrado"
        );
    }

    private void configurarComponentes() {
        comentariosTextArea.setPromptText("Agrega comentario (opcional)...");
        comentariosTextArea.clear();
        
        listaTicketsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarEstadoActual();
                cargarComentarios();
            }
        });
    }

    private void actualizarEstadoActual() {
        String ticketSeleccionado = listaTicketsComboBox.getValue();
        if (ticketSeleccionado != null && estadosActuales.containsKey(ticketSeleccionado)) {
            String estadoActual = estadosActuales.get(ticketSeleccionado);
            estadoTicketComboBox.setValue(estadoActual);
        }
    }

    private void cargarComentarios() {
        String ticketSeleccionado = listaTicketsComboBox.getValue();
        if (ticketSeleccionado != null) {
            String comentarios = historialComentarios.getOrDefault(ticketSeleccionado, "");
            comentariosTextArea.setText(comentarios);
        }
    }

    
    @FXML
    private void handleGuardarAction() {
        String ticketSeleccionado = listaTicketsComboBox.getValue();
        String nuevoEstado = estadoTicketComboBox.getValue();
        String nuevoComentario = comentariosTextArea.getText().trim();

        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            mostrarMensajeError("Error", "Debe seleccionar un estado válido");
            return;
        }

        
        estadosActuales.put(ticketSeleccionado, nuevoEstado);

        
        if (!nuevoComentario.isEmpty()) {
            String comentarioActualizado = historialComentarios.getOrDefault(ticketSeleccionado, "") 
                + "\n\n" + LocalDateTime.now() + ": " + nuevoComentario;
            historialComentarios.put(ticketSeleccionado, comentarioActualizado.trim());
        }

       
        try (PrintWriter writer = new PrintWriter(new FileWriter("EstadoFinalTicket.txt", true))) {
            writer.println("=== Ticket Actualizado ===");
            writer.println("Fecha/Hora Actualización: " + LocalDateTime.now());
            writer.println("Ticket: " + ticketSeleccionado);
            writer.println("Estado Final: " + nuevoEstado);
            writer.println("Comentarios: " + (nuevoComentario.isEmpty() ? "Ninguno" : nuevoComentario));
            writer.println("========================");
            writer.println(); 
            
            HistorialLogger.registrarAccion("Ticket en proceso de guardar", 
                "Ticket guardado: " + ticketSeleccionado);
            
        } catch (IOException e) {
            HistorialLogger.registrarAccion("Error", "No se pudo guardar el estado final del ticket: " + e.getMessage());
            mostrarMensajeError("Error", "No se pudo guardar el estado final del ticket");
            return;
        }

        mostrarMensaje("Actualizacion de Ticket hecho", 
            "El estado se ha cambiado a: " + nuevoEstado +
            (nuevoComentario.isEmpty() ? "" : "\n\nComentario agregado") +
            "\n\nInformación guardada");
        
        habilitarEdicion(false);
        
        HistorialLogger.registrarAccion("Actualizacion Ticket", 
            "Ticket: " + ticketSeleccionado + 
            ", Nuevo estad: " + nuevoEstado);
    }


    private void habilitarEdicion(boolean habilitar) {
        estadoTicketComboBox.setDisable(!habilitar);
        comentariosTextArea.setDisable(!habilitar);
        guardarButton.setDisable(!habilitar);
        editarButton.setDisable(habilitar);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}