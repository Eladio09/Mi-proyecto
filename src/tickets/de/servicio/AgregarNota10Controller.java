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
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class AgregarNota10Controller implements Initializable {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextArea txtNotaObligatoria;
    
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private ComboBox<String> cbPrioridad;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbListaTickets;
    
    @FXML private Button btnExaminar;
    @FXML private Button btnMostrar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;
    
    private File archivoAdjunto;
    private List<String> listaTickets = new ArrayList<>();
    private String ticketActual = null;

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void eventExaminar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento adjunto");
        archivoAdjunto = fileChooser.showOpenDialog(btnExaminar.getScene().getWindow());
        if (archivoAdjunto != null) {
            mostrarAlerta("Éxito", "Archivo adjunto seleccionado: " + archivoAdjunto.getName(), Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void eventMostrar(ActionEvent event) {
        String ticketSeleccionado = cbListaTickets.getValue();
        if (ticketSeleccionado != null && !ticketSeleccionado.equals("Nuevo Ticket")) {
            ticketActual = ticketSeleccionado;
            cargarDatosTicket(ticketSeleccionado);
            mostrarAlerta("Información", "Ticket cargado correctamente", Alert.AlertType.INFORMATION);
        } else if (ticketSeleccionado != null && ticketSeleccionado.equals("Nuevo Ticket")) {
            ticketActual = null;
            limpiarCampos();
        }
    }
    
    @FXML
    private void eventGuardar(ActionEvent event) {
        if (txtNotaObligatoria.getText().isEmpty()) {
            mostrarAlerta("Error", "La nota es obligatoria", Alert.AlertType.ERROR);
            return;
        }
        
        if (ticketActual != null) {
            Optional<ButtonType> resultado = mostrarConfirmacion("¿Desea actualizar el ticket seleccionado?");
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                if (actualizarTicket()) {
                    mostrarAlerta("Éxito", "Ticket actualizado correctamente", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar el ticket", Alert.AlertType.ERROR);
                }
            }
        } else {
            mostrarAlerta("Error", "No hay ticket seleccionado para actualizar", Alert.AlertType.ERROR);
        }
    }
    
    private Optional<ButtonType> mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait();
    }
    
    @FXML
    private void eventRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuOperaciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo regresar al menú: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbDepartamento.getItems().addAll("TI", "Ventas");
        cbPrioridad.getItems().addAll("Alta", "Media", "Baja");
        cbEstado.getItems().addAll("Abierto", "En progreso", "Cerrado");
        
        cargarListaTickets();
        cbListaTickets.getItems().add("Nuevo Ticket");
        cbListaTickets.getItems().addAll(listaTickets);
        cbListaTickets.getSelectionModel().selectFirst();
    }
    
    private void cargarListaTickets() {
        listaTickets.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("NuevoTicket.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("-Ticket Nuevo-")) {
                    while ((linea = reader.readLine()) != null) {
                        if (linea.startsWith("Título: ")) {
                            listaTickets.add(linea.substring("Título: ".length()).trim());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la lista de tickets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void cargarDatosTicket(String tituloTicket) {
        try (BufferedReader reader = new BufferedReader(new FileReader("NuevoTicket.txt"))) {
            Map<String, String> datosTicket = new HashMap<>();
            String linea;
            boolean ticketEncontrado = false;
            StringBuilder notas = new StringBuilder();
            
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("-Ticket Nuevo-")) {
                    while ((linea = reader.readLine()) != null && !linea.startsWith("Título: ")) {
                    }
                    
                    if (linea != null && linea.substring("Título: ".length()).trim().equals(tituloTicket)) {
                        ticketEncontrado = true;
                        datosTicket.put("Título", tituloTicket);
                        
                        while ((linea = reader.readLine()) != null && !linea.startsWith("-Ticket Nuevo-")) {
                            if (linea.startsWith("Descripción: ")) {
                                datosTicket.put("Descripción", linea.substring("Descripción: ".length()).trim());
                            } else if (linea.startsWith("Departamento: ")) {
                                datosTicket.put("Departamento", linea.substring("Departamento: ".length()).trim());
                            } else if (linea.startsWith("Prioridad: ")) {
                                datosTicket.put("Prioridad", linea.substring("Prioridad: ".length()).trim());
                            } else if (linea.startsWith("Estado: ")) {
                                datosTicket.put("Estado", linea.substring("Estado: ".length()).trim());
                            } else if (linea.startsWith("Notas: ")) {
                                notas.append(linea.substring("Notas: ".length()).trim()).append("\n");
                                while ((linea = reader.readLine()) != null && !linea.startsWith("-Ticket Nuevo-") && 
                                       !linea.startsWith("Descripción: ") && !linea.startsWith("Departamento: ") && 
                                       !linea.startsWith("Prioridad: ") && !linea.startsWith("Estado: ")) {
                                    notas.append(linea).append("\n");
                                }
                                if (linea != null && !linea.startsWith("-Ticket Nuevo-")) {
                                    datosTicket.put(linea.split(": ")[0], linea.split(": ")[1]);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            
            if (ticketEncontrado) {
                txtTitulo.setText(datosTicket.getOrDefault("Título", ""));
                txtDescripcion.setText(datosTicket.getOrDefault("Descripción", ""));
                cbDepartamento.getSelectionModel().select(datosTicket.get("Departamento"));
                cbPrioridad.getSelectionModel().select(datosTicket.get("Prioridad"));
                cbEstado.getSelectionModel().select(datosTicket.get("Estado"));
                txtNotaObligatoria.setText(notas.toString().trim());
            } else {
                mostrarAlerta("Error", "Ticket no encontrado: " + tituloTicket, Alert.AlertType.ERROR);
                limpiarCampos();
            }
            
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al leer el ticket: " + e.getMessage(), Alert.AlertType.ERROR);
            limpiarCampos();
        }
    }
    
    private boolean actualizarTicket() {
        try {
            File archivo = new File("NuevoTicket.txt");
            File tempFile = new File("temp.txt");
            
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            
            String linea;
            boolean enTicketActual = false;
            boolean ticketModificado = false;
            
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("-Ticket Nuevo-")) {
                    String posibleTitulo = reader.readLine(); 
                    posibleTitulo = reader.readLine(); 
                    
                    if (posibleTitulo != null && posibleTitulo.startsWith("Título: ") && 
                        posibleTitulo.substring("Título: ".length()).trim().equals(ticketActual)) {
                        
                        writer.write("-Ticket Nuevo-\n");
                        writer.write("Fecha/Hora: " + LocalDateTime.now() + "\n");
                        writer.write("Título: " + txtTitulo.getText() + "\n");
                        writer.write("Descripción: " + txtDescripcion.getText() + "\n");
                        writer.write("Departamento: " + cbDepartamento.getValue() + "\n");
                        writer.write("Prioridad: " + cbPrioridad.getValue() + "\n");
                        writer.write("Estado: " + cbEstado.getValue() + "\n");
                        
                        if (!txtNotaObligatoria.getText().isEmpty()) {
                            writer.write("Notas: " + txtNotaObligatoria.getText() + "\n");
                        }
                        
                        writer.write("\n");
                        
                        while ((linea = reader.readLine()) != null && !linea.startsWith("-Ticket Nuevo-")) {
                        }
                        
                        if (linea != null) {
                            writer.write(linea + "\n");
                        }
                        
                        ticketModificado = true;
                    } else {
                        writer.write("-Ticket Nuevo-\n");
                        writer.write(linea + "\n");
                        if (posibleTitulo != null) {
                            writer.write(posibleTitulo + "\n");
                        }
                    }
                } else {
                    if (!enTicketActual) {
                        writer.write(linea + "\n");
                    }
                }
            }
            
            reader.close();
            writer.close();
            
            if (ticketModificado) {
                if (archivo.delete()) {
                    return tempFile.renameTo(archivo);
                }
            }
            
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al actualizar el ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return false;
    }
    
    private void limpiarCampos() {
        txtTitulo.clear();
        txtDescripcion.clear();
        txtNotaObligatoria.clear();
        cbDepartamento.getSelectionModel().clearSelection();
        cbPrioridad.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();
    }
}