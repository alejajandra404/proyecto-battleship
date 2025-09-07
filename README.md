# 🚢 Batalla Naval - Arquitectura de Software

Un juego multijugador en línea del clásico **Battleship** desarrollado como proyecto del curso de Arquitectura de Software, implementando diferentes patrones y estilos arquitectónicos.

## 📋 Descripción del Proyecto

Implementación digital del tradicional juego de mesa "Batalla Naval" donde dos jugadores compiten para hundir la flota enemiga a través de disparos estratégicos en un tablero de 10x10 casillas.

## 🎯 Objetivo Académico

Aplicar diferentes **patrones y estilos arquitectónicos** para resolver los requerimientos del juego, demostrando principios de diseño de software escalable y mantenible.

## ⚓ Características del Juego

### Flota de Naves
Cada jugador cuenta con:
- **2 Portaaviones** (4 casillas)
- **2 Cruceros** (3 casillas) 
- **4 Submarinos** (2 casillas)
- **3 Barcos** (1 casilla)

### Mecánicas de Juego
- **Tablero**: Matriz de 10x10 casillas
- **Colocación**: Arrastrar y soltar naves con rotación
- **Turnos alternados** con selección aleatoria del primer jugador
- **Tiempo límite**: 30 segundos por disparo
- **Sistema de puntuación**: Impacto = turno adicional
- **Estados de nave**: Sin daños, Averiada, Hundida

## 🎮 Cómo Jugar
### Preparación
1. Cada jugador especifica su nombre y selecciona un color
2. Colocar las naves en el tablero 
3. Rotar las naves según sea necesario
4. Confirmar la posición de la flota (no se puede modificar después)

### Durante la Batalla
1. El juego selecciona aleatoriamente quién inicia
2. El jugador en turno hace clic en una casilla del tablero enemigo para disparar
3. Se anuncia el resultado:
   - **Agua**: Disparo fallido, pasa el turno
   - **Impacto**: Disparo exitoso, obtiene turno adicional
4. Si todas las casillas de una nave son impactadas, la nave se hunde
5. El jugador tiene 30 segundos máximo por disparo

### Condiciones de Victoria
- Gana quien destruya primero todas las naves del oponente
- Si un jugador abandona, el oponente es declarado ganador automáticamente

### Reglas Importantes
- Las naves no pueden colocarse en casillas adyacentes
- No se puede disparar a coordenadas ya atacadas
- Los disparos fuera del tablero son inválidos
- Cada impacto exitoso otorga un turno adicional
