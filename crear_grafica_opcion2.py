#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
import numpy as np

# Datos de la gráfica 100x100
tamaños_pagina = [256, 512, 1024, 2048, 4096]
fallos_pagina = [535, 301, 184, 126, 98]

# Datos de las configuraciones adicionales
configuraciones = ['Config 1', 'Config 2', 'Config 3', 'Config 4']
tasa_fallos_config = [0.39, 0.80, 0.20, 1.58]

# Crear figura con subplots
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))

# Gráfica 1: Fallos de página vs Tamaño de página (100x100)
ax1.plot(tamaños_pagina, fallos_pagina, 'bo-', linewidth=2, markersize=8, label='Fallos de página')
ax1.set_xlabel('Tamaño de Página (bytes)', fontsize=12)
ax1.set_ylabel('Número de Fallos de Página', fontsize=12)
ax1.set_title('Fallos de Página vs Tamaño de Página\n(Matrices 100x100)', fontsize=14, fontweight='bold')
ax1.grid(True, alpha=0.3)
ax1.set_xscale('log', base=2)
ax1.set_xticks(tamaños_pagina)
ax1.set_xticklabels([str(x) for x in tamaños_pagina])

# Añadir valores en los puntos
for i, (x, y) in enumerate(zip(tamaños_pagina, fallos_pagina)):
    ax1.annotate(f'{y}', (x, y), textcoords="offset points", xytext=(0,10), ha='center', fontsize=10)

# Gráfica 2: Tasa de fallos por configuración
colors = ['green', 'orange', 'blue', 'red']
bars = ax2.bar(configuraciones, tasa_fallos_config, color=colors, alpha=0.7, edgecolor='black', linewidth=1)
ax2.set_xlabel('Configuraciones Adicionales', fontsize=12)
ax2.set_ylabel('Tasa de Fallos (%)', fontsize=12)
ax2.set_title('Tasa de Fallos por Configuración\n(Opción 2)', fontsize=14, fontweight='bold')
ax2.grid(True, alpha=0.3, axis='y')

# Añadir valores en las barras
for bar, value in zip(bars, tasa_fallos_config):
    height = bar.get_height()
    ax2.annotate(f'{value}%', xy=(bar.get_x() + bar.get_width()/2, height),
                xytext=(0, 3), textcoords="offset points", ha='center', va='bottom', fontsize=10)

# Añadir línea de referencia en 1%
ax2.axhline(y=1.0, color='red', linestyle='--', alpha=0.5, label='Línea de referencia (1%)')
ax2.legend()

# Ajustar layout
plt.tight_layout()

# Guardar la gráfica
plt.savefig('grafica_opcion2.png', dpi=300, bbox_inches='tight')
plt.savefig('grafica_opcion2.pdf', bbox_inches='tight')

# Mostrar la gráfica
plt.show()

print("Gráfica generada y guardada como 'grafica_opcion2.png' y 'grafica_opcion2.pdf'")
print("\n=== ANÁLISIS DE LA GRÁFICA ===")
print("Gráfica 1 (Izquierda):")
print("- Muestra la relación inversa entre tamaño de página y fallos")
print("- Páginas más grandes = menos fallos de página")
print("- Reducción del 81.7% de fallos (535 → 98)")

print("\nGráfica 2 (Derecha):")
print("- Config 3 (2048 bytes) es la más eficiente: 0.20%")
print("- Config 4 (256 bytes) es la menos eficiente: 1.58%")
print("- Config 1 y 2 tienen rendimiento intermedio")
