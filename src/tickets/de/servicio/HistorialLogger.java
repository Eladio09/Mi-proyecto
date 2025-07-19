 //Si logras resolverlo, dime como al 37-76-71-01.
//Y dile a mi padre, que yo tampoco me siento orgulloso.
//Di lo mejor de mi pero el codigo pudo mas.
package tickets.de.servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author rolbi
 */
public class HistorialLogger {
   
    private static final List<String> historial = new ArrayList<>();
    
    
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Constructor
    private HistorialLogger() {}

    
    public static void registrarAccion(String modulo, String accion) {
        String entrada = String.format("[%s] %s - %s", 
            LocalDateTime.now().format(formatter), 
            modulo, 
            accion);
        
        synchronized (historial) {
            historial.add(entrada);
        }
    }

    
    public static String obtenerHistorialCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("**** HISTORIAL DEL SISTEMA ***\n\n");
        
        synchronized (historial) {
            if (historial.isEmpty()) {
                sb.append("No hay registros en el historial\n");
            } else {
                for (String entrada : historial) {
                    sb.append(entrada).append("\n");
                }
            }
        }
        
        return sb.toString();
    }


    public static String filtrarPorModulo(String modulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("**** HISTORIAL DE ").append(modulo.toUpperCase()).append(" ===\n\n");
        
        synchronized (historial) {
            for (String entrada : historial) {
                if (entrada.contains("] " + modulo + " - ")) {
                    sb.append(entrada).append("\n");
                }
            }
        }
        
        return sb.toString();
    }

    
    public static void limpiarHistorial() {
        synchronized (historial) {
            historial.clear();
        }
    }
 
    public static int obtenerCantidadRegistros() {
        synchronized (historial) {
            return historial.size();
        }
    }
}