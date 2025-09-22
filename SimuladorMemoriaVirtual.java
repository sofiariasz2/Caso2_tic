import java.io.*;
import java.util.*;

/**
 * Simulador de Memoria Virtual
 * Universidad de los Andes - ISIS 1311
 * 
 * Este simulador genera las direcciones virtuales que producirían procesos
 * al ejecutar la suma de matrices, considerando el almacenamiento en memoria
 * y la paginación virtual.
 * 
 * Opción 1: Generación de referencias
 * Opción 2: Simulación de la ejecución
 */
public class SimuladorMemoriaVirtual {
    
    // Constantes
    private static final int TAMANO_ENTERO = 4; // bytes
    private static final String ARCHIVO_CONFIG = "config.txt";
    private static final String ARCHIVO_CONFIG_OPCION2 = "config_opcion2.txt";
    
    // Variables de configuración
    private int tamanoPagina;      // TP
    private int numeroProcesos;    // NPROC
    private int[] tamanosMatrices; // TAMS
    
    // Variables para Opción 2
    private int totalMarcosRAM;    // Total de marcos en RAM
    private List<Proceso> procesos;
    private GestorMemoria gestorMemoria;
    
    /**
     * Constructor principal
     */
    public SimuladorMemoriaVirtual() {
        this.tamanosMatrices = new int[0];
    }
    
    /**
     * Método principal que ejecuta el simulador
     */
    public static void main(String[] args) {
        SimuladorMemoriaVirtual simulador = new SimuladorMemoriaVirtual();
        
        try {
            System.out.println("=== SIMULADOR DE MEMORIA VIRTUAL ===");
            System.out.println("Seleccione una opción:");
            System.out.println("1. Generación de referencias (Opción 1)");
            System.out.println("2. Simulación de ejecución (Opción 2)");
            System.out.print("Ingrese su opción (1 o 2): ");
            
            Scanner scanner = new Scanner(System.in);
            int opcion = scanner.nextInt();
            
            if (opcion == 1) {
                simulador.ejecutarOpcion1();
            } else if (opcion == 2) {
                simulador.ejecutarOpcion2();
            } else {
                System.err.println("Opción inválida. Debe ser 1 o 2.");
                return;
            }
            
        } catch (Exception e) {
            System.err.println("Error durante la simulación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ejecuta la Opción 1: Generación de referencias
     */
    private void ejecutarOpcion1() throws IOException {
        System.out.println("\n=== OPCIÓN 1: GENERACIÓN DE REFERENCIAS ===");
        System.out.println("Iniciando simulación...\n");
        
        // Leer configuración
        leerConfiguracion();
        
        // Mostrar configuración leída
        mostrarConfiguracion();
        
        // Generar archivos para cada proceso
        generarArchivosProcesos();
        
        System.out.println("\n=== SIMULACIÓN COMPLETADA ===");
        System.out.println("Se generaron " + numeroProcesos + " archivos de proceso.");
    }
    
    /**
     * Ejecuta la Opción 2: Simulación de ejecución
     */
    private void ejecutarOpcion2() throws IOException {
        System.out.println("\n=== OPCIÓN 2: SIMULACIÓN DE EJECUCIÓN ===");
        System.out.println("Iniciando simulación...\n");
        
        // Leer configuración para Opción 2
        leerConfiguracionOpcion2();
        
        // Mostrar configuración leída
        mostrarConfiguracionOpcion2();
        
        // Inicializar gestor de memoria
        gestorMemoria = new GestorMemoria(tamanoPagina, totalMarcosRAM);
        
        // Cargar procesos desde archivos generados por Opción 1
        cargarProcesos();
        
        // Ejecutar simulación
        simularEjecucion();
        
        // Mostrar estadísticas finales
        mostrarEstadisticasFinales();
        
        System.out.println("\n=== SIMULACIÓN COMPLETADA ===");
    }
    
    /**
     * Lee el archivo de configuración
     */
    private void leerConfiguracion() throws IOException {
        File archivo = new File(ARCHIVO_CONFIG);
        
        if (!archivo.exists()) {
            throw new FileNotFoundException("No se encontró el archivo de configuración: " + ARCHIVO_CONFIG);
        }
        
        Scanner scanner = new Scanner(archivo);
        
        try {
            // Leer tamaño de página (TP)
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Error: TP debe ser un número entero");
            }
            this.tamanoPagina = scanner.nextInt();
            
            // Leer número de procesos (NPROC)
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Error: NPROC debe ser un número entero");
            }
            this.numeroProcesos = scanner.nextInt();
            
            // Leer tamaños de matrices (TAMS)
            this.tamanosMatrices = new int[this.numeroProcesos];
            for (int i = 0; i < this.numeroProcesos; i++) {
                if (!scanner.hasNextInt()) {
                    throw new IllegalArgumentException("Error: TAMS[" + i + "] debe ser un número entero");
                }
                this.tamanosMatrices[i] = scanner.nextInt();
            }
            
        } finally {
            scanner.close();
        }
        
        // Validaciones
        if (this.tamanoPagina <= 0) {
            throw new IllegalArgumentException("Error: TP debe ser mayor que 0");
        }
        if (this.numeroProcesos <= 0) {
            throw new IllegalArgumentException("Error: NPROC debe ser mayor que 0");
        }
        for (int i = 0; i < this.numeroProcesos; i++) {
            if (this.tamanosMatrices[i] <= 0) {
                throw new IllegalArgumentException("Error: TAMS[" + i + "] debe ser mayor que 0");
            }
        }
    }
    
    /**
     * Muestra la configuración leída
     */
    private void mostrarConfiguracion() {
        System.out.println("Configuración leída:");
        System.out.println("  TP (Tamaño de página): " + tamanoPagina + " bytes");
        System.out.println("  NPROC (Número de procesos): " + numeroProcesos);
        System.out.println("  TAMS (Tamaños de matrices):");
        for (int i = 0; i < numeroProcesos; i++) {
            System.out.println("    Proceso " + i + ": " + tamanosMatrices[i] + "x" + tamanosMatrices[i]);
        }
        System.out.println();
    }
    
    /**
     * Genera los archivos de salida para todos los procesos
     */
    private void generarArchivosProcesos() throws IOException {
        for (int i = 0; i < numeroProcesos; i++) {
            generarArchivoProceso(i, tamanosMatrices[i]);
        }
    }
    
    /**
     * Genera el archivo de salida para un proceso específico
     */
    private void generarArchivoProceso(int numeroProceso, int tamanoMatriz) throws IOException {
        String nombreArchivo = "proc" + numeroProceso + ".txt";
        PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo));
        
        try {
            // Calcular métricas del proceso
            int numeroFilas = tamanoMatriz;
            int numeroColumnas = tamanoMatriz;
            int numeroReferencias = calcularNumeroReferencias(numeroFilas, numeroColumnas);
            int numeroPaginasVirtuales = calcularNumeroPaginasVirtuales(numeroFilas, numeroColumnas);
            
            // Escribir encabezado del archivo en formato del anexo
            writer.println("TP=" + tamanoPagina);           // TP=128
            writer.println("NF=" + numeroFilas);            // NF=4
            writer.println("NC=" + numeroColumnas);         // NC=4
            writer.println("NR=" + numeroReferencias);      // NR=48
            writer.println("NP=" + numeroPaginasVirtuales); // NP=2
            
            // Generar y escribir referencias en formato del anexo
            generarReferenciasFormatoAnexo(writer, numeroFilas, numeroColumnas);
            
            System.out.println("Archivo generado: " + nombreArchivo + 
                             " (Referencias: " + numeroReferencias + 
                             ", Páginas: " + numeroPaginasVirtuales + ")");
            
        } finally {
            writer.close();
        }
    }
    
    /**
     * Calcula el número de referencias que generará el proceso
     * Cada iteración del doble for genera 3 accesos: matriz1[i][j], matriz2[i][j], matriz3[i][j]
     */
    private int calcularNumeroReferencias(int filas, int columnas) {
        return filas * columnas * 3; // 3 matrices por cada elemento
    }
    
    /**
     * Calcula el número de páginas virtuales necesarias para el proceso
     */
    private int calcularNumeroPaginasVirtuales(int filas, int columnas) {
        // Cada matriz ocupa: filas * columnas * 4 bytes
        int bytesPorMatriz = filas * columnas * TAMANO_ENTERO;
        int bytesTotales = bytesPorMatriz * 3; // 3 matrices
        
        // Calcular páginas necesarias (redondeo hacia arriba)
        int paginasNecesarias = (bytesTotales + tamanoPagina - 1) / tamanoPagina;
        
        return paginasNecesarias;
    }
    
    /**
     * Genera las direcciones virtuales que produciría el proceso
     * Simula el acceso a memoria del código: matriz3[i][j] = matriz1[i][j] + matriz2[i][j]
     */
    private List<Integer> generarDireccionesVirtuales(int filas, int columnas) {
        List<Integer> direcciones = new ArrayList<>();
        
        // Calcular direcciones base de cada matriz
        int bytesPorMatriz = filas * columnas * TAMANO_ENTERO;
        int direccionBaseMatriz1 = 0;
        int direccionBaseMatriz2 = direccionBaseMatriz1 + bytesPorMatriz;
        int direccionBaseMatriz3 = direccionBaseMatriz2 + bytesPorMatriz;
        
        // Simular el doble for del código
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Calcular offset dentro de la matriz (row-major order)
                int offset = (i * columnas + j) * TAMANO_ENTERO;
                
                // Generar las 3 direcciones virtuales por iteración:
                // 1. Lectura de matriz1[i][j]
                direcciones.add(direccionBaseMatriz1 + offset);
                
                // 2. Lectura de matriz2[i][j]
                direcciones.add(direccionBaseMatriz2 + offset);
                
                // 3. Escritura de matriz3[i][j]
                direcciones.add(direccionBaseMatriz3 + offset);
            }
        }
        
        return direcciones;
    }
    
    /**
     * Genera las referencias en el formato del anexo
     * Formato: M1:[i-j],pagina,offset,operacion
     */
    private void generarReferenciasFormatoAnexo(PrintWriter writer, int filas, int columnas) throws IOException {
        // Calcular direcciones base de cada matriz
        int bytesPorMatriz = filas * columnas * TAMANO_ENTERO;
        int direccionBaseMatriz1 = 0;
        int direccionBaseMatriz2 = direccionBaseMatriz1 + bytesPorMatriz;
        int direccionBaseMatriz3 = direccionBaseMatriz2 + bytesPorMatriz;
        
        // Simular el doble for del código
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Calcular offset dentro de la matriz (row-major order)
                int offset = (i * columnas + j) * TAMANO_ENTERO;
                
                // 1. Lectura de matriz1[i][j]
                int direccion1 = direccionBaseMatriz1 + offset;
                int pagina1 = direccion1 / tamanoPagina;
                int offsetPagina1 = direccion1 % tamanoPagina;
                writer.println("M1:[" + i + "-" + j + "]," + pagina1 + "," + offsetPagina1 + ",r");
                
                // 2. Lectura de matriz2[i][j]
                int direccion2 = direccionBaseMatriz2 + offset;
                int pagina2 = direccion2 / tamanoPagina;
                int offsetPagina2 = direccion2 % tamanoPagina;
                writer.println("M2:[" + i + "-" + j + "]," + pagina2 + "," + offsetPagina2 + ",r");
                
                // 3. Escritura de matriz3[i][j]
                int direccion3 = direccionBaseMatriz3 + offset;
                int pagina3 = direccion3 / tamanoPagina;
                int offsetPagina3 = direccion3 % tamanoPagina;
                writer.println("M3:[" + i + "-" + j + "]," + pagina3 + "," + offsetPagina3 + ",w");
            }
        }
    }
    
    // ==================== MÉTODOS PARA OPCIÓN 2 ====================
    
    /**
     * Lee el archivo de configuración para la Opción 2
     */
    private void leerConfiguracionOpcion2() throws IOException {
        File archivo = new File(ARCHIVO_CONFIG_OPCION2);
        
        if (!archivo.exists()) {
            throw new FileNotFoundException("No se encontró el archivo de configuración: " + ARCHIVO_CONFIG_OPCION2);
        }
        
        Scanner scanner = new Scanner(archivo);
        
        try {
            // Leer tamaño de página (TP)
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Error: TP debe ser un número entero");
            }
            this.tamanoPagina = scanner.nextInt();
            
            // Leer número de procesos (NPROC)
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Error: NPROC debe ser un número entero");
            }
            this.numeroProcesos = scanner.nextInt();
            
            // Leer total de marcos en RAM
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException("Error: Total de marcos debe ser un número entero");
            }
            this.totalMarcosRAM = scanner.nextInt();
            
        } finally {
            scanner.close();
        }
        
        // Validaciones
        if (this.tamanoPagina <= 0) {
            throw new IllegalArgumentException("Error: TP debe ser mayor que 0");
        }
        if (this.numeroProcesos <= 0) {
            throw new IllegalArgumentException("Error: NPROC debe ser mayor que 0");
        }
        if (this.totalMarcosRAM <= 0) {
            throw new IllegalArgumentException("Error: Total de marcos debe ser mayor que 0");
        }
        if (this.totalMarcosRAM % this.numeroProcesos != 0) {
            throw new IllegalArgumentException("Error: Total de marcos debe ser múltiplo del número de procesos");
        }
    }
    
    /**
     * Muestra la configuración leída para Opción 2
     */
    private void mostrarConfiguracionOpcion2() {
        System.out.println("Configuración leída:");
        System.out.println("  TP (Tamaño de página): " + tamanoPagina + " bytes");
        System.out.println("  NPROC (Número de procesos): " + numeroProcesos);
        System.out.println("  Total de marcos en RAM: " + totalMarcosRAM);
        System.out.println("  Marcos por proceso: " + (totalMarcosRAM / numeroProcesos));
        System.out.println();
    }
    
    /**
     * Carga los procesos desde los archivos generados por la Opción 1
     */
    private void cargarProcesos() throws IOException {
        procesos = new ArrayList<>();
        
        for (int i = 0; i < numeroProcesos; i++) {
            String nombreArchivo = "proc" + i + ".txt";
            File archivo = new File(nombreArchivo);
            
            if (!archivo.exists()) {
                throw new FileNotFoundException("No se encontró el archivo: " + nombreArchivo + 
                    ". Ejecute primero la Opción 1.");
            }
            
            List<Integer> direccionesVirtuales = leerDireccionesVirtuales(nombreArchivo);
            Proceso proceso = new Proceso(i, direccionesVirtuales);
            procesos.add(proceso);
            
            System.out.println("Proceso " + i + " cargado: " + direccionesVirtuales.size() + " direcciones virtuales");
        }
        
        // Asignar marcos equitativamente
        for (Proceso proceso : procesos) {
            Set<Integer> marcosAsignados = gestorMemoria.asignarMarcosEquitativos(numeroProcesos, proceso.getId());
            proceso.asignarMarcos(marcosAsignados);
            System.out.println("Proceso " + proceso.getId() + " asignado " + marcosAsignados.size() + " marcos");
        }
        
        System.out.println();
    }
    
    /**
     * Lee las direcciones virtuales de un archivo de proceso
     */
    private List<Integer> leerDireccionesVirtuales(String nombreArchivo) throws IOException {
        List<Integer> direcciones = new ArrayList<>();
        Scanner scanner = new Scanner(new File(nombreArchivo));
        
        try {
            // Saltar las primeras 5 líneas (TP=, NF=, NC=, NR=, NP=)
            for (int i = 0; i < 5; i++) {
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
            
            // Leer referencias en formato del anexo y extraer direcciones virtuales
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                if (linea.contains(",")) {
                    // Formato: M1:[i-j],pagina,offset,operacion
                    String[] partes = linea.split(",");
                    if (partes.length >= 2) {
                        int pagina = Integer.parseInt(partes[1]);
                        int offset = Integer.parseInt(partes[2]);
                        int direccion = pagina * tamanoPagina + offset;
                        direcciones.add(direccion);
                    }
                }
            }
            
        } finally {
            scanner.close();
        }
        
        return direcciones;
    }
    
    /**
     * Simula la ejecución de los procesos con política de turnos
     */
    private void simularEjecucion() {
        System.out.println("Iniciando simulación de ejecución...");
        
        Queue<Proceso> colaProcesos = new LinkedList<>(procesos);
        int turno = 0;
        
        while (!colaProcesos.isEmpty()) {
            Proceso procesoActual = colaProcesos.poll();
            
            if (procesoActual.haTerminado()) {
                // Proceso terminado, reasignar sus marcos
                Set<Integer> marcosLiberados = procesoActual.obtenerMarcosAsignados();
                gestorMemoria.liberarMarcos(marcosLiberados);
                gestorMemoria.reasignarMarcos(marcosLiberados, procesos);
                System.out.println("Proceso " + procesoActual.getId() + " terminado. Marcos reasignados.");
                continue;
            }
            
            // Procesar siguiente dirección virtual
            Integer direccionVirtual = procesoActual.obtenerSiguienteDireccion();
            if (direccionVirtual == null) {
                continue;
            }
            
            // Convertir dirección virtual a página
            int paginaVirtual = procesoActual.direccionAPagina(direccionVirtual, tamanoPagina);
            
            // Verificar si hay fallo de página
            boolean falloPagina = !procesoActual.paginaEnMemoria(paginaVirtual);
            
            if (falloPagina) {
                // Intentar cargar página en memoria
                int marcoAsignado = gestorMemoria.cargarPagina(paginaVirtual, procesoActual.obtenerMarcosAsignados());
                
                if (marcoAsignado != -1) {
                    // Página cargada exitosamente
                    procesoActual.cargarPagina(paginaVirtual, marcoAsignado);
                    
                    // Contar accesos SWAP
                    if (gestorMemoria.getMarcosLibres() > 0) {
                        // Fallo sin reemplazo: 1 acceso SWAP
                        procesoActual.incrementarAccesosSWAP(1);
                    } else {
                        // Fallo con reemplazo: 2 accesos SWAP
                        procesoActual.incrementarAccesosSWAP(2);
                    }
                    
                    procesoActual.incrementarFallosPagina();
                    
                    // Reinsertar proceso al final de la cola (demorar un turno)
                    colaProcesos.offer(procesoActual);
                    
                    System.out.println("Turno " + turno + ": Proceso " + procesoActual.getId() + 
                        " - Fallo de página en dirección " + direccionVirtual + " (página " + paginaVirtual + ")");
                } else {
                    // No se pudo cargar la página (no debería pasar)
                    System.err.println("Error: No se pudo cargar página " + paginaVirtual + 
                        " para proceso " + procesoActual.getId());
                }
            } else {
                // Acierto: página ya en memoria
                procesoActual.procesarDireccion();
                
                // Reinsertar proceso si no ha terminado
                if (!procesoActual.haTerminado()) {
                    colaProcesos.offer(procesoActual);
                }
                
                if (turno % 100 == 0) { // Mostrar progreso cada 100 turnos
                    System.out.println("Turno " + turno + ": Proceso " + procesoActual.getId() + 
                        " - Acierto en dirección " + direccionVirtual);
                }
            }
            
            turno++;
        }
        
        System.out.println("Simulación completada en " + turno + " turnos.");
    }
    
    /**
     * Muestra las estadísticas finales de todos los procesos
     */
    private void mostrarEstadisticasFinales() {
        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        System.out.println(gestorMemoria.obtenerEstadisticas());
        System.out.println();
        
        // Mostrar estadísticas por proceso en formato del anexo
        for (Proceso proceso : procesos) {
            System.out.println("Proceso: " + proceso.getId());
            System.out.println("Num referencias: " + proceso.getTotalReferencias());
            System.out.println("Fallas: " + proceso.getTotalFallosPagina());
            System.out.println("Hits: " + (proceso.getTotalReferencias() - proceso.getTotalFallosPagina()));
            System.out.println("SWAP: " + proceso.getTotalAccesosSWAP());
            System.out.println("Tasa fallas: " + String.format("%.4f", proceso.getTasaFallosPagina()));
            System.out.println("Tasa éxito: " + String.format("%.4f", proceso.getTasaAciertos()));
            System.out.println();
        }
        
        // Estadísticas globales
        int totalReferencias = procesos.stream().mapToInt(Proceso::getTotalReferencias).sum();
        int totalFallos = procesos.stream().mapToInt(Proceso::getTotalFallosPagina).sum();
        int totalSWAP = procesos.stream().mapToInt(Proceso::getTotalAccesosSWAP).sum();
        
        System.out.println("=== ESTADÍSTICAS GLOBALES ===");
        System.out.println("Total de referencias: " + totalReferencias);
        System.out.println("Total de fallos de página: " + totalFallos);
        System.out.println("Total de accesos SWAP: " + totalSWAP);
        System.out.println("Tasa global de fallos: " + String.format("%.4f", (double) totalFallos / totalReferencias));
        System.out.println("Tasa global de aciertos: " + String.format("%.4f", (double) (totalReferencias - totalFallos) / totalReferencias));
    }
}
