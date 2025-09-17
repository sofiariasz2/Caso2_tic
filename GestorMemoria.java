import java.util.*;

/**
 * Gestor de memoria física que implementa el algoritmo LRU
 * para el reemplazo de páginas en el simulador de memoria virtual
 */
public class GestorMemoria {
    
    private int tamanoPagina;
    private int totalMarcos;
    private Map<Integer, Integer> marcoAPagina; // marco físico -> página virtual
    private Map<Integer, Integer> paginaAMarco; // página virtual -> marco físico
    private Map<Integer, Long> ultimoAccesoMarco; // marco físico -> último acceso
    private Set<Integer> marcosLibres;
    private long contadorTiempo;
    
    /**
     * Constructor del gestor de memoria
     */
    public GestorMemoria(int tamanoPagina, int totalMarcos) {
        this.tamanoPagina = tamanoPagina;
        this.totalMarcos = totalMarcos;
        this.marcoAPagina = new HashMap<>();
        this.paginaAMarco = new HashMap<>();
        this.ultimoAccesoMarco = new HashMap<>();
        this.marcosLibres = new HashSet<>();
        this.contadorTiempo = 0;
        
        // Inicializar todos los marcos como libres
        for (int i = 0; i < totalMarcos; i++) {
            marcosLibres.add(i);
        }
    }
    
    /**
     * Asigna marcos a un proceso de manera equitativa
     */
    public Set<Integer> asignarMarcosEquitativos(int numeroProcesos, int idProceso) {
        Set<Integer> marcosAsignados = new HashSet<>();
        int marcosPorProceso = totalMarcos / numeroProcesos;
        int inicio = idProceso * marcosPorProceso;
        int fin = inicio + marcosPorProceso;
        
        for (int i = inicio; i < fin; i++) {
            marcosAsignados.add(i);
            marcosLibres.remove(i);
        }
        
        return marcosAsignados;
    }
    
    /**
     * Libera marcos de un proceso
     */
    public void liberarMarcos(Set<Integer> marcos) {
        for (int marco : marcos) {
            // Remover página del marco si existe
            Integer pagina = marcoAPagina.remove(marco);
            if (pagina != null) {
                paginaAMarco.remove(pagina);
                ultimoAccesoMarco.remove(marco);
            }
            
            // Marcar marco como libre
            marcosLibres.add(marco);
        }
    }
    
    /**
     * Reasigna marcos liberados al proceso con más fallos de página
     */
    public void reasignarMarcos(Set<Integer> marcosLiberados, List<Proceso> procesos) {
        if (marcosLiberados.isEmpty()) return;
        
        // Encontrar el proceso con más fallos de página
        Proceso procesoConMasFallos = null;
        int maxFallos = -1;
        
        for (Proceso proceso : procesos) {
            if (!proceso.haTerminado() && proceso.getTotalFallosPagina() > maxFallos) {
                maxFallos = proceso.getTotalFallosPagina();
                procesoConMasFallos = proceso;
            }
        }
        
        // Asignar marcos al proceso con más fallos
        if (procesoConMasFallos != null) {
            procesoConMasFallos.asignarMarcos(marcosLiberados);
            marcosLibres.removeAll(marcosLiberados);
        } else {
            // Si no hay procesos activos, marcar marcos como libres
            marcosLibres.addAll(marcosLiberados);
        }
    }
    
    /**
     * Intenta cargar una página en memoria
     * @param paginaVirtual Página virtual a cargar
     * @param marcosDisponibles Marcos disponibles para el proceso
     * @return Marco físico donde se cargó la página, o -1 si no se pudo cargar
     */
    public int cargarPagina(int paginaVirtual, Set<Integer> marcosDisponibles) {
        // Verificar si la página ya está cargada
        if (paginaAMarco.containsKey(paginaVirtual)) {
            int marco = paginaAMarco.get(paginaVirtual);
            actualizarAcceso(marco);
            return marco;
        }
        
        // Buscar marco libre en los marcos disponibles del proceso
        for (int marco : marcosDisponibles) {
            if (marcosLibres.contains(marco)) {
                return asignarPaginaAMarco(paginaVirtual, marco);
            }
        }
        
        // Buscar marco ocupado en los marcos disponibles del proceso para reemplazar
        for (int marco : marcosDisponibles) {
            if (marcoAPagina.containsKey(marco)) {
                return asignarPaginaAMarco(paginaVirtual, marco);
            }
        }
        
        // Si no hay marcos ocupados, usar cualquier marco disponible
        for (int marco : marcosDisponibles) {
            return asignarPaginaAMarco(paginaVirtual, marco);
        }
        
        return -1; // No se pudo cargar
    }
    
    /**
     * Asigna una página a un marco específico
     */
    private int asignarPaginaAMarco(int paginaVirtual, int marco) {
        // Remover página anterior del marco si existe
        Integer paginaAnterior = marcoAPagina.remove(marco);
        if (paginaAnterior != null) {
            paginaAMarco.remove(paginaAnterior);
        }
        
        // Asignar nueva página al marco
        marcoAPagina.put(marco, paginaVirtual);
        paginaAMarco.put(paginaVirtual, marco);
        ultimoAccesoMarco.put(marco, contadorTiempo++);
        
        // Remover marco de libres si estaba ahí
        marcosLibres.remove(marco);
        
        return marco;
    }
    
    /**
     * Reemplaza una página usando algoritmo LRU
     */
    private int reemplazarPaginaLRU(int paginaVirtual, Set<Integer> marcosDisponibles) {
        // Encontrar el marco menos recientemente usado entre los disponibles
        int marcoLRU = -1;
        long menorTiempo = Long.MAX_VALUE;
        
        for (int marco : marcosDisponibles) {
            if (marcoAPagina.containsKey(marco)) {
                long tiempoAcceso = ultimoAccesoMarco.getOrDefault(marco, 0L);
                if (tiempoAcceso < menorTiempo) {
                    menorTiempo = tiempoAcceso;
                    marcoLRU = marco;
                }
            }
        }
        
        if (marcoLRU != -1) {
            return asignarPaginaAMarco(paginaVirtual, marcoLRU);
        }
        
        return -1; // No se pudo reemplazar
    }
    
    /**
     * Actualiza el tiempo de acceso de un marco
     */
    private void actualizarAcceso(int marco) {
        ultimoAccesoMarco.put(marco, contadorTiempo++);
    }
    
    /**
     * Verifica si una página está cargada en memoria
     */
    public boolean paginaEnMemoria(int paginaVirtual) {
        return paginaAMarco.containsKey(paginaVirtual);
    }
    
    /**
     * Obtiene el marco físico donde está cargada una página
     */
    public Integer obtenerMarcoFisico(int paginaVirtual) {
        return paginaAMarco.get(paginaVirtual);
    }
    
    /**
     * Obtiene estadísticas de memoria
     */
    public String obtenerEstadisticas() {
        int marcosOcupados = totalMarcos - marcosLibres.size();
        return String.format(
            "Memoria: %d/%d marcos ocupados, %d marcos libres",
            marcosOcupados, totalMarcos, marcosLibres.size()
        );
    }
    
    // Getters
    public int getTotalMarcos() { return totalMarcos; }
    public int getMarcosLibres() { return marcosLibres.size(); }
    public int getMarcosOcupados() { return totalMarcos - marcosLibres.size(); }
}
