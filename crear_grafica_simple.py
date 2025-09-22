#!/usr/bin/env python3

# Script simple para crear gráfica de fallas vs tamaño de página
# Matrices 100x100

# Datos obtenidos de la simulación
datos = {
    'Tamaño_Pagina': [256, 512, 1024, 2048, 4096],
    'Fallas_Pagina': [535, 301, 184, 126, 98]
}

print("=== GRÁFICA: FALLAS DE PÁGINA VS TAMAÑO DE PÁGINA ===")
print("Matrices 100x100")
print("")

# Crear representación ASCII de la gráfica
max_fallas = max(datos['Fallas_Pagina'])
max_tp = max(datos['Tamaño_Pagina'])

print("Tamaño de Página (bytes) | Fallas de Página | Gráfica")
print("-" * 60)

for i, (tp, fallas) in enumerate(zip(datos['Tamaño_Pagina'], datos['Fallas_Pagina'])):
    # Calcular altura de la barra (máximo 20 caracteres)
    altura = int((fallas / max_fallas) * 20)
    barra = "█" * altura
    
    print(f"{tp:>20} | {fallas:>15} | {barra}")

print("")
print("Análisis:")
print("- A mayor tamaño de página, menor número de fallas")
print("- Relación inversa: más datos por página = menos fallos")
print("- Páginas grandes aprovechan mejor la localidad espacial")
print("- Matrices 100x100 tienen excelente localidad espacial")

# Guardar datos en archivo
with open('datos_grafica_100x100.txt', 'w') as f:
    f.write("=== DATOS PARA GRÁFICA: FALLAS VS TAMAÑO DE PÁGINA ===\n")
    f.write("Matrices 100x100\n\n")
    f.write("Tamaño_Pagina,Fallas_Pagina\n")
    for tp, fallas in zip(datos['Tamaño_Pagina'], datos['Fallas_Pagina']):
        f.write(f"{tp},{fallas}\n")
    
    f.write("\n=== ANÁLISIS ===\n")
    f.write("- A mayor tamaño de página, menor número de fallas\n")
    f.write("- Relación inversa: más datos por página = menos fallos\n")
    f.write("- Páginas grandes aprovechan mejor la localidad espacial\n")
    f.write("- Matrices 100x100 tienen excelente localidad espacial\n")

print("Datos guardados en 'datos_grafica_100x100.txt'")
