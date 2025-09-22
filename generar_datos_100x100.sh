#!/bin/bash

echo "=== GENERANDO DATOS PARA MATRICES 100x100 ==="
echo "Variando tamaño de página: 256, 512, 1024, 2048, 4096 bytes"
echo ""

# Crear archivo de resultados
echo "Tamaño_Pagina,Fallas_Pagina" > resultados_100x100.csv

# Configuraciones a probar
configs=("256" "512" "1024" "2048" "4096")

for tp in "${configs[@]}"; do
    echo "Procesando tamaño de página: $tp bytes"
    
    # Copiar configuración
    cp "config_100x100_${tp}.txt" config.txt
    
    # Generar referencias (Opción 1)
    echo "1" | java SimuladorMemoriaVirtual > /dev/null 2>&1
    
    # Ejecutar simulación (Opción 2) - usar configuración simple
    echo "2" | java SimuladorMemoriaVirtual > temp_output.txt 2>&1
    
    # Extraer número de fallos
    fallos=$(grep "Total de fallos de página:" temp_output.txt | awk '{print $6}')
    
    if [ -z "$fallos" ]; then
        fallos="0"
    fi
    
    echo "Tamaño de página: $tp bytes, Fallos: $fallos"
    echo "$tp,$fallos" >> resultados_100x100.csv
    
    echo ""
done

# Limpiar archivo temporal
rm -f temp_output.txt

echo "=== RESULTADOS GUARDADOS EN resultados_100x100.csv ==="
echo ""
echo "Contenido del archivo:"
cat resultados_100x100.csv
