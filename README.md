# Simulador de Memoria Virtual

## Descripción
Este proyecto implementa un simulador de memoria virtual que genera las direcciones virtuales que producirían procesos al ejecutar la suma de matrices, considerando el almacenamiento en memoria y la paginación virtual.

## Archivos del Proyecto

### 1. SimuladorMemoriaVirtual.java
Clase principal que contiene toda la lógica del simulador.

### 2. config.txt
Archivo de configuración con los parámetros de entrada:
- **TP**: Tamaño de página en bytes (1024)
- **NPROC**: Número de procesos (3)
- **TAMS**: Tamaños de matrices para cada proceso (10, 20, 5)

### 3. Archivos de salida generados
- `proc0.txt`: Direcciones virtuales del proceso 0 (matriz 10x10)
- `proc1.txt`: Direcciones virtuales del proceso 1 (matriz 20x20)
- `proc2.txt`: Direcciones virtuales del proceso 2 (matriz 5x5)

## Cómo usar el simulador

### Compilación
```bash
javac SimuladorMemoriaVirtual.java
```

### Ejecución
```bash
java SimuladorMemoriaVirtual
```

## Formato del archivo de configuración
```
TP
NPROC
TAMS[0]
TAMS[1]
...
TAMS[NPROC-1]
```

## Formato de los archivos de salida
Cada archivo `proc<i>.txt` contiene:
1. **TP**: Tamaño de página
2. **NF**: Número de filas de la matriz
3. **NC**: Número de columnas de la matriz
4. **NR**: Número de referencias generadas
5. **NP**: Número de páginas virtuales necesarias
6. **Direcciones virtuales**: Lista de todas las direcciones virtuales generadas

## Ejemplo de ejecución
```
=== SIMULADOR DE MEMORIA VIRTUAL ===
Iniciando simulación...

Configuración leída:
  TP (Tamaño de página): 1024 bytes
  NPROC (Número de procesos): 3
  TAMS (Tamaños de matrices):
    Proceso 0: 10x10
    Proceso 1: 20x20
    Proceso 2: 5x5

Archivo generado: proc0.txt (Referencias: 300, Páginas: 2)
Archivo generado: proc1.txt (Referencias: 1200, Páginas: 5)
Archivo generado: proc2.txt (Referencias: 75, Páginas: 1)

=== SIMULACIÓN COMPLETADA ===
Se generaron 3 archivos de proceso.
```

## Características técnicas

- **Almacenamiento**: Las matrices se almacenan en orden fila-mayor (row-major)
- **Orden de matrices**: matriz1, matriz2, matriz3 consecutivamente en memoria
- **Tamaño de entero**: 4 bytes
- **Cálculo de páginas**: Se calcula el número de páginas virtuales necesarias basado en el tamaño total de las 3 matrices
- **Generación de direcciones**: Simula exactamente el patrón de acceso del código de suma de matrices

## Validaciones implementadas
- Verificación de existencia del archivo de configuración
- Validación de que todos los parámetros sean números enteros positivos
- Manejo de errores con mensajes descriptivos
