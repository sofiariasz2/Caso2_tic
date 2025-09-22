#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Datos de la gráfica 100x100
tamaños_pagina = [256, 512, 1024, 2048, 4096]
fallos_pagina = [535, 301, 184, 126, 98]

# Datos de las configuraciones adicionales
configuraciones = ['Config 1', 'Config 2', 'Config 3', 'Config 4']
tasa_fallos_config = [0.39, 0.80, 0.20, 1.58]

print("=== GRÁFICA OPCIÓN 2: ANÁLISIS DE MEMORIA VIRTUAL ===\n")

# Gráfica 1: Fallos de página vs Tamaño de página (ASCII)
print("📊 GRÁFICA 1: FALLOS DE PÁGINA VS TAMAÑO DE PÁGINA (Matrices 100x100)")
print("=" * 70)
print("Tamaño Página (bytes) | Fallos de Página | Gráfica ASCII")
print("-" * 70)

max_fallos = max(fallos_pagina)
for i, (tamaño, fallos) in enumerate(zip(tamaños_pagina, fallos_pagina)):
    # Crear barra ASCII
    barra_length = int((fallos / max_fallos) * 40)
    barra = "█" * barra_length
    
    print(f"{tamaño:>8} bytes        | {fallos:>8} fallos    | {barra} {fallos}")

print("\n📈 TENDENCIA: RELACIÓN INVERSA")
print("   • Páginas más grandes = Menos fallos de página")
print("   • Reducción del 81.7% (535 → 98 fallos)")
print("   • Mejor aprovechamiento de localidad espacial")

print("\n" + "=" * 70)

# Gráfica 2: Tasa de fallos por configuración (ASCII)
print("\n📊 GRÁFICA 2: TASA DE FALLOS POR CONFIGURACIÓN (Opción 2)")
print("=" * 70)
print("Configuración | Tasa Fallos (%) | Gráfica ASCII | Eficiencia")
print("-" * 70)

max_tasa = max(tasa_fallos_config)
for i, (config, tasa) in enumerate(zip(configuraciones, tasa_fallos_config)):
    # Crear barra ASCII
    barra_length = int((tasa / max_tasa) * 30)
    barra = "█" * barra_length
    
    # Determinar eficiencia
    if tasa <= 0.5:
        eficiencia = "🟢 EXCELENTE"
    elif tasa <= 1.0:
        eficiencia = "🟡 BUENA"
    else:
        eficiencia = "🔴 REGULAR"
    
    print(f"{config:>12} | {tasa:>8.2f}%      | {barra} {tasa}% | {eficiencia}")

print("\n🏆 CONFIGURACIÓN ÓPTIMA: Config 3 (0.20% fallos)")
print("   • Tamaño de página: 2048 bytes")
print("   • Número de procesos: 3")
print("   • Matrices grandes: 80x80 a 100x100")

print("\n" + "=" * 70)

# Datos para Excel/Google Sheets
print("\n📋 DATOS PARA CREAR GRÁFICA EN EXCEL/GOOGLE SHEETS:")
print("=" * 70)
print("Tamaño Página (bytes),Fallos de Página")
for tamaño, fallos in zip(tamaños_pagina, fallos_pagina):
    print(f"{tamaño},{fallos}")

print("\nConfiguración,Tasa Fallos (%)")
for config, tasa in zip(configuraciones, tasa_fallos_config):
    print(f"{config},{tasa}")

print("\n" + "=" * 70)

# Análisis detallado
print("\n🔍 ANÁLISIS DETALLADO:")
print("=" * 70)
print("1. RELACIÓN INVERSA CONFIRMADA:")
print("   • 256 bytes → 535 fallos (peor rendimiento)")
print("   • 4096 bytes → 98 fallos (mejor rendimiento)")
print("   • Reducción progresiva y consistente")

print("\n2. EFICIENCIA MARGINAL DECRECIENTE:")
print("   • 256→512 bytes: -43.7% fallos")
print("   • 512→1024 bytes: -38.9% fallos")
print("   • 1024→2048 bytes: -31.5% fallos")
print("   • 2048→4096 bytes: -22.2% fallos")

print("\n3. CONFIGURACIÓN ÓPTIMA IDENTIFICADA:")
print("   • Config 3: 0.20% fallos (más eficiente)")
print("   • Config 4: 1.58% fallos (menos eficiente)")
print("   • Diferencia de 8x en eficiencia")

print("\n4. JUSTIFICACIÓN TÉCNICA:")
print("   • Páginas grandes aprovechan mejor localidad espacial")
print("   • Menos cambios de página durante procesamiento")
print("   • Algoritmo LRU más efectivo con páginas grandes")
print("   • Acceso secuencial (row-major) optimizado")

print("\n" + "=" * 70)
print("✅ GRÁFICA GENERADA EXITOSAMENTE")
print("📁 Usa los datos CSV para crear gráficas en Excel/Google Sheets")
print("🎯 Configuración recomendada: 2048 bytes, 3 procesos, matrices grandes")
