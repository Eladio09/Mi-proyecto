//Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import tickets.de.servicio.HistorialLogger;
/**
 *
 * @author rolbi
 */


public class ConfigurarController implements Initializable {
    
    @FXML private Button btnIrAMenu;
    @FXML private TextField nombreEmpresaField;
    @FXML private ComboBox<String> zonaHorariaCombo;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> tiempoTicketCombo;
    @FXML private ComboBox<String> idiomaCombo;
    @FXML private ComboBox<String> prioridadTicketCombo;
    @FXML private Button btnExaminarLogo;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegresar;
    
    private Image imagenPorDefecto;
    private boolean imagenCargada = false;
    private boolean configuracionCompleta = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HistorialLogger.registrarAccion("Configuración", "Pantalla de configuración inicializada");
        imagenPorDefecto = logoImageView.getImage();
        
        logoImageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            imagenCargada = (newImage != null && !newImage.isError() && newImage != imagenPorDefecto);
        });
        
        inicializarZonasHorarias();
        inicializarTiemposTicket();
        inicializarIdiomas();
        inicializarPrioridades();
        cargarConfiguracion();
        deshabilitarBotonMenu();
    }

        @FXML
    private void eventRegresar(ActionEvent event){
        try {
            HistorialLogger.registrarAccion("Configuracion", "Regresando a pantalla de Login");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Configuracion", "Error al regresar a ogin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void inicializarZonasHorarias() {
        zonaHorariaCombo.getItems().addAll("Guatemala (GMT-6/-5)");
        zonaHorariaCombo.setPromptText("Zona horaria");
    }
    
    private void inicializarTiemposTicket() {
        tiempoTicketCombo.getItems().addAll(
            "4 horas", "8 horas", "12 horas", 
            "24 horas", "48 horas", "72 horas"
        );
        tiempoTicketCombo.setPromptText("Seleccione un tiempo inicial");
    }
    
    private void inicializarIdiomas() {
        idiomaCombo.getItems().addAll("Español");
        idiomaCombo.setPromptText("Seleccione idioma");
    }
    
    private void inicializarPrioridades() {
        prioridadTicketCombo.getItems().addAll("Baja", "Media", "Alta");
        prioridadTicketCombo.setPromptText("Seleccione prioridad");
    }

    @FXML
    private void handleExaminarLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione un Logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(logoImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                logoImageView.setImage(image);
                imagenCargada = true;
                logoImageView.setStyle("");
                HistorialLogger.registrarAccion("Configuracion", "Logo cargado con exito: " + selectedFile.getName());
            } catch (Exception e) {
                HistorialLogger.registrarAccion("Configuracion", "Error al cargar logo: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo cargar la imagen, seleccione otra", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            try {
                
                guardarConfiguracionEnArchivo();
                
                configuracionCompleta = true;
                habilitarBotonMenu();
                HistorialLogger.registrarAccion("Configuracion", 
                    "Configuracion guardada: " + 
                    "Empresa=" + nombreEmpresaField.getText() + ", " +
                    "Zona=" + zonaHorariaCombo.getValue() + ", " +
                    "Tiempo=" + tiempoTicketCombo.getValue() + ", " +
                    "Idioma=" + idiomaCombo.getValue() + ", " +
                    "Prioridad=" + prioridadTicketCombo.getValue());
                mostrarAlerta("Exito", "Configuracion guardada correctamente. Ahora puede acceder al menu.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                HistorialLogger.registrarAccion("Configuracion", "Error al guardar configuracion: " + e.getMessage());
                mostrarAlerta("Error", "Ocurrio un error para guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void guardarConfiguracionEnArchivo() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter("Configuraciones.txt", true))) {
            out.println("=== Configuracion Guardada y almacenada ===");
            out.println("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            out.println("Empresa: " + nombreEmpresaField.getText());
            out.println("Zona Horaria: " + zonaHorariaCombo.getValue());
            out.println("Tiempo Ticket: " + tiempoTicketCombo.getValue());
            out.println("Idioma: " + idiomaCombo.getValue());
            out.println("Prioridad: " + prioridadTicketCombo.getValue());
            out.println("Logo cargado: " + (imagenCargada ? "Sí" : "No"));
            out.println("=============================");
            out.println();
        }
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean valido = true;
        
        if (nombreEmpresaField.getText().trim().isEmpty()) {
            errores.append("- Nombre de la empresa es requerido\n");
            nombreEmpresaField.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            nombreEmpresaField.setStyle("");
        }
        
        if (zonaHorariaCombo.getValue() == null) {
            errores.append("- Zona horaria es requerida\n");
            zonaHorariaCombo.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            zonaHorariaCombo.setStyle("");
        }
        
        if (!imagenCargada) {
            errores.append("- Debe cargar un logo\n");
            logoImageView.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            logoImageView.setStyle("");
        }
        
        if (tiempoTicketCombo.getValue() == null) {
            errores.append("- Tiempo de respuesta es requerido\n");
            tiempoTicketCombo.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            tiempoTicketCombo.setStyle("");
        }
        
        if (idiomaCombo.getValue() == null) {
            errores.append("- Idioma es requerido\n");
            idiomaCombo.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            idiomaCombo.setStyle("");
        }
        
        if (prioridadTicketCombo.getValue() == null) {
            errores.append("- Prioridad por defecto es requerida\n");
            prioridadTicketCombo.setStyle("-fx-border-color: red;");
            valido = false;
        } else {
            prioridadTicketCombo.setStyle("");
        }
        
        if (!valido) {
            mostrarAlerta("Error", "Por favor complete todos los campos obligatorios:\n\n" + errores.toString(), 
                        Alert.AlertType.ERROR);
        }
        
        return valido;
    }

    @FXML
    private void handleCancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelacion");
        confirmacion.setHeaderText("¿Esta seguro que desea cancelar los cambios?");
        confirmacion.setContentText("Todos los cambios no guardados se perderan.");
        
        ButtonType btnSi = new ButtonType("Si", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                HistorialLogger.registrarAccion("Configuracion", "Configuracion cancelada - cambios descartados");
                cargarConfiguracion();
                logoImageView.setImage(imagenPorDefecto);
                imagenCargada = false;
                configuracionCompleta = false;
                deshabilitarBotonMenu();
                mostrarAlerta("Informacion", "Se han descartado todos los cambios", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    private void cargarConfiguracion() {
        nombreEmpresaField.setText("");
        zonaHorariaCombo.getSelectionModel().clearSelection();
        tiempoTicketCombo.getSelectionModel().clearSelection();
        idiomaCombo.getSelectionModel().clearSelection();
        prioridadTicketCombo.getSelectionModel().clearSelection();
        
        nombreEmpresaField.setStyle("");
        zonaHorariaCombo.setStyle("");
        logoImageView.setStyle("");
        tiempoTicketCombo.setStyle("");
        idiomaCombo.setStyle("");
        prioridadTicketCombo.setStyle("");
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void deshabilitarBotonMenu() {
        btnIrAMenu.setDisable(true);
        btnIrAMenu.setText("Ir al Menu (Complete la configuracion primero)");
        btnIrAMenu.setStyle("-fx-opacity: 0.7;");
    }
    
    private void habilitarBotonMenu() {
        btnIrAMenu.setDisable(false);
        btnIrAMenu.setText("Ir al Menu");
        btnIrAMenu.setStyle("");
    }

    @FXML 
    private void handleIrAMenu() { 
        if (validarAccesoMenu()) {
            HistorialLogger.registrarAccion("Configuracion", "Accediendo al menu principal");
            cambiarPantalla("MenuOperaciones.fxml");
        }
    }

    private boolean validarAccesoMenu() {
        if (!configuracionCompleta) {
            mostrarAlerta("Configuracion incompleta", 
                        "Debe completar y guardar toda la configuracion antes de acceder al menu.", 
                        Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void cambiarPantalla(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) btnIrAMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            HistorialLogger.registrarAccion("Configuracion", "Error al cambiar a pantalla: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la pantalla: " + fxmlFile, Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


}