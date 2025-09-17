import java.util.*;

/**
 * Clase que representa un proceso en el simulador de memoria virtual
 * Cada proceso tiene su propia tabla de páginas y marcos asignados
 */
public class Proceso {
    
    private int id;
    private List<Integer> direccionesVirtuales;
    private Map<Integer, Integer> tablaPaginas; // página virtual -> marco físico
    private Set<Integer> marcosAsignados;
    private int indiceDireccionActual;
    private int totalReferencias;
    private int totalFallosPagina;
    private int totalAccesosSWAP;
    private long ultimoAcceso; // Para algoritmo LRU
    
    /**
     * Constructor del proceso
     */
    public Proceso(int id, List<Integer> direccionesVirtuales) {
        this.id = id;
        this.direccionesVirtuales = new ArrayList<>(direccionesVirtuales);
        this.tablaPaginas = new HashMap<>();
        this.marcosAsignados = new HashSet<>();
        this.indiceDireccionActual = 0;
        this.totalReferencias = 0;
        this.totalFallosPagina = 0;
        this.totalAccesosSWAP = 0;
        this.ultimoAcceso = 0;
    }
    
    /**
     * Obtiene la siguiente dirección virtual a procesar
     */
    public Integer obtenerSiguienteDireccion() {
        if (indiceDireccionActual >= direccionesVirtuales.size()) {
            return null; // No hay más direcciones
        }
        return direccionesVirtuales.get(indiceDireccionActual);
    }
    
    /**
     * Marca la dirección actual como procesada
     */
    public void procesarDireccion() {
        indiceDireccionActual++;
        totalReferencias++;
    }
    
    /**
     * Verifica si el proceso ha terminado
     */
    public boolean haTerminado() {
        return indiceDireccionActual >= direccionesVirtuales.size();
    }
    
    /**
     * Convierte una dirección virtual en número de página
     */
    public int direccionAPagina(int direccionVirtual, int tamanoPagina) {
        return direccionVirtual / tamanoPagina;
    }
    
    /**
     * Verifica si una página está cargada en memoria
     */
    public boolean paginaEnMemoria(int paginaVirtual) {
        return tablaPaginas.containsKey(paginaVirtual);
    }
    
    /**
     * Obtiene el marco físico donde está cargada una página
     */
    public Integer obtenerMarcoFisico(int paginaVirtual) {
        return tablaPaginas.get(paginaVirtual);
    }
    
    /**
     * Carga una página en un marco físico
     */
    public void cargarPagina(int paginaVirtual, int marcoFisico) {
        tablaPaginas.put(paginaVirtual, marcoFisico);
        marcosAsignados.add(marcoFisico);
    }
    
    /**
     * Descarga una página de memoria
     */
    public void descargarPagina(int paginaVirtual) {
        Integer marcoFisico = tablaPaginas.remove(paginaVirtual);
        if (marcoFisico != null) {
            marcosAsignados.remove(marcoFisico);
        }
    }
    
    /**
     * Obtiene todas las páginas cargadas en memoria
     */
    public Set<Integer> obtenerPaginasCargadas() {
        return new HashSet<>(tablaPaginas.keySet());
    }
    
    /**
     * Obtiene los marcos asignados al proceso
     */
    public Set<Integer> obtenerMarcosAsignados() {
        return new HashSet<>(marcosAsignados);
    }
    
    /**
     * Asigna marcos al proceso
     */
    public void asignarMarcos(Set<Integer> marcos) {
        this.marcosAsignados.addAll(marcos);
    }
    
    /**
     * Libera marcos del proceso
     */
    public void liberarMarcos(Set<Integer> marcos) {
        this.marcosAsignados.removeAll(marcos);
        // Remover páginas que estaban en esos marcos
        Iterator<Map.Entry<Integer, Integer>> it = tablaPaginas.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            if (marcos.contains(entry.getValue())) {
                it.remove();
            }
        }
    }
    
    /**
     * Incrementa contador de fallos de página
     */
    public void incrementarFallosPagina() {
        totalFallosPagina++;
    }
    
    /**
     * Incrementa contador de accesos SWAP
     */
    public void incrementarAccesosSWAP(int cantidad) {
        totalAccesosSWAP += cantidad;
    }
    
    /**
     * Actualiza el último acceso para LRU
     */
    public void actualizarUltimoAcceso(long tiempo) {
        this.ultimoAcceso = tiempo;
    }
    
    // Getters
    public int getId() { return id; }
    public int getTotalReferencias() { return totalReferencias; }
    public int getTotalFallosPagina() { return totalFallosPagina; }
    public int getTotalAccesosSWAP() { return totalAccesosSWAP; }
    public long getUltimoAcceso() { return ultimoAcceso; }
    public int getNumeroMarcosAsignados() { return marcosAsignados.size(); }
    
    /**
     * Calcula la tasa de fallos de página
     */
    public double getTasaFallosPagina() {
        if (totalReferencias == 0) return 0.0;
        return (double) totalFallosPagina / totalReferencias;
    }
    
    /**
     * Calcula la tasa de aciertos
     */
    public double getTasaAciertos() {
        if (totalReferencias == 0) return 0.0;
        return (double) (totalReferencias - totalFallosPagina) / totalReferencias;
    }
    
    /**
     * Obtiene estadísticas del proceso
     */
    public String obtenerEstadisticas() {
        return String.format(
            "Proceso %d: Referencias=%d, Fallos=%d, SWAP=%d, TasaFallos=%.3f, TasaAciertos=%.3f",
            id, totalReferencias, totalFallosPagina, totalAccesosSWAP, 
            getTasaFallosPagina(), getTasaAciertos()
        );
    }
}
