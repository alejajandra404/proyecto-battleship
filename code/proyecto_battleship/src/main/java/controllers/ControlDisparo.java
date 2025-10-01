package controllers;

import dtos.*;
import enums.ResultadoDisparo;
import exceptions.CasillaException;
import exceptions.TableroException;
import java.util.Timer;
import java.util.TimerTask;
import models.*;
import utils.MapperDTO;
/**
 * ControlDisparo.java
 *
 * Clase que representa el controlador del caso de uso
 * principal: Realizar Disparo
 * 
 * BAJO ACOPLAMIENTO: 
 * - Recibe DTOs de la vista 
 * - Convierte DTOs a objetos del modelo 
 * - Devuelve DTOs a la vista
 *
 * ALTA COHESIÓN: 
 * - Solo se encarga de procesar disparos 
 * - Toda la conversión DTO-Modelo está centralizada aquí
 * 
 * @author Leonardo Flores Leyva
 * ID: 00000252390
 * @author Yuri Germán García López
 * ID: 00000252583
 * @author Alejandra García Preciado
 * ID: 00000252444
 * @author Jesús Ernesto López Ibarra
 * ID: 00000252663
 * @author Daniel Miramontes Iribe
 * ID: 00000252801
 *
 */
public class ControlDisparo {
   
    private Partida partidaActual;
    private Timer temporizador;
    private TimerTask tareaActual;
    private int tiempoRestante;

    private static final int TIEMPO_TURNO_SEGUNDOS = 30;
    
    /**
     * Constructor del controlador
     * 
     * @param partida
     */
    public ControlDisparo(Partida partida){
        this.partidaActual = partida;
        this.tiempoRestante = TIEMPO_TURNO_SEGUNDOS;
    }
    
    /**
     * Procesa un disparo usando DTOs
     *
     * @param disparoDTO
     * @return DisparoDTO con el resultado del disparo
     * @throws TableroException
     * @throws CasillaException
     */
    public DisparoDTO procesarDisparo(DisparoDTO disparoDTO) 
            throws TableroException, CasillaException{
        
        // PASO 1: Pausar el temporizador
        pausarTemporizador();

        // PASO 2: Convertir DTO a entidad del modelo
        Coordenada coordenada = MapperDTO.toEntity(disparoDTO.getCoordenada());

        // PASO 3: Buscar el jugador por nombre (del DTO)
        Jugador jugador = buscarJugadorPorNombre(disparoDTO.getNombreJugador());

        if (jugador == null) {
            reanudarTemporizador();
            disparoDTO.setResultado(null);
            disparoDTO.setMensaje("Jugador no encontrado");
            return disparoDTO;
        }

        // PASO 4: Verificar que es el turno del jugador
        if (!partidaActual.verificarJugadorTurno(jugador)) {
            reanudarTemporizador();
            disparoDTO.setResultado(null);
            disparoDTO.setMensaje("No es tu turno de atacar");
            return disparoDTO;
        }

        // PASO 5: Validar el disparo
        if (!partidaActual.validarDisparo(coordenada, jugador)) {
            reanudarTemporizador();
            disparoDTO.setResultado(null);
            disparoDTO.setMensaje("❌ Coordenada inválida o ya disparada");
            notificarResultado("❌ Coordenada inválida");
            return disparoDTO;
        }

        // PASO 6: Realizar el disparo
        boolean impacto = partidaActual.recibirDisparo(coordenada, jugador);

        // PASO 7: Actualizar el DTO con el resultado
        if (impacto) {
            disparoDTO.setResultado(ResultadoDisparo.IMPACTO);
            disparoDTO.setMensaje("🎯 ¡IMPACTO! Sigue tu turno");
            notificarResultado("🎯 ¡IMPACTO!");
            // NO cambiar turno si fue impacto
        } else {
            disparoDTO.setResultado(ResultadoDisparo.AGUA);
            disparoDTO.setMensaje("💧 Agua. Turno del oponente");
            notificarResultado("💧 Agua");
            // Cambiar turno si fue agua
            partidaActual.cambiarTurno();
        }

        // PASO 8: Reiniciar temporizador
        reiniciarTemporizador();

        return disparoDTO;
    }
    
    /**
     * Busca un jugador por nombre
     */
    private Jugador buscarJugadorPorNombre(String nombre) {
        if (partidaActual.getJugador1().getNombre().equals(nombre)) {
            return partidaActual.getJugador1();
        } else if (partidaActual.getJugador2().getNombre().equals(nombre)) {
            return partidaActual.getJugador2();
        }
        return null;
    }
    
    /**
     * Obtiene el estado actual de la partida como DTO
     * 
     * @return 
     */
    public EstadoPartidaDTO obtenerEstadoPartida() {
        return MapperDTO.crearEstadoPartidaDTO(partidaActual, tiempoRestante);
    }

    /**
     * Obtiene información del turno actual como DTO
     * 
     * @return 
     */
    public TurnoDTO obtenerTurnoActual() {
        String nombreJugador = partidaActual.obtenerJugadorTurno().getNombre();
        String mensaje = "Turno de " + nombreJugador;
        return MapperDTO.crearTurnoDTO(partidaActual, tiempoRestante, mensaje);
    }
    
    /**
     * Actualiza el turno (cambia al siguiente jugador) Devuelve un TurnoDTO con
     * la información del nuevo turno
     * 
     * @return 
     */
    public TurnoDTO actualizarTurno() {
        partidaActual.cambiarTurno();
        reiniciarTemporizador();

        String nombreNuevoTurno = partidaActual.obtenerJugadorTurno().getNombre();
        String mensaje = "🔄 Turno de " + nombreNuevoTurno;

        notificarResultado(mensaje);

        return MapperDTO.crearTurnoDTO(partidaActual, tiempoRestante, mensaje);
    }

    /**
     * Notifica un resultado o mensaje
     * 
     * @param mensaje
     */
    public void notificarResultado(String mensaje) {
        String nombreJugadorTurno = partidaActual.obtenerJugadorTurno().getNombre();
        partidaActual.notificarObservadores(mensaje);
    }

    /**
     * Inicia el temporizador de 30 segundos
     */
    public void iniciarTemporizador() {
        detenerTemporizador();

        tiempoRestante = TIEMPO_TURNO_SEGUNDOS;
        temporizador = new Timer();

        tareaActual = new TimerTask() {
            @Override
            public void run() {
                tiempoRestante--;

                // Verificar tiempo agotado
                if (tiempoRestante <= 0) {
                    manejarTiempoAgotado();
                }
            }
        };

        temporizador.scheduleAtFixedRate(tareaActual, 1000, 1000);
    }

    /**
     * Maneja el flujo alternativo FA4 - Tiempo agotado
     */
    private void manejarTiempoAgotado() {
        detenerTemporizador();
        String mensaje = "⏰ Tiempo agotado. Turno perdido";
        notificarResultado(mensaje);
        
        // Esperar un momento antes de cambiar turno para que el usuario vea la notificación
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        actualizarTurno();
    }

    /**
     * Pausa el temporizador
     */
    public void pausarTemporizador() {
        if (tareaActual != null) {
            tareaActual.cancel();
        }
    }

    /**
     * Reanuda el temporizador
     */
    public void reanudarTemporizador() {
        if (temporizador != null && tiempoRestante > 0) {
            iniciarTemporizador();
        }
    }

    /**
     * Reinicia el temporizador al valor inicial
     */
    public void reiniciarTemporizador() {
        detenerTemporizador();
        iniciarTemporizador();
    }

    /**
     * Detiene completamente el temporizador
     */
    private void detenerTemporizador() {
        if (tareaActual != null) {
            tareaActual.cancel();
            tareaActual = null;
        }
        if (temporizador != null) {
            temporizador.cancel();
            temporizador.purge();
            temporizador = null;
        }
    }

    /**
     * Obtiene el tiempo restante
     * 
     * @return 
     */
    public int getTiempoRestante() {
        return tiempoRestante;
    }

    /**
     * Verifica si el tiempo ha expirado
     * 
     * @return 
     */
    public boolean verificarExpiracion() {
        return tiempoRestante <= 0;
    }

    /**
     * Obtiene la partida actual
     * 
     * @return 
     */
    public Partida getPartida() {
        return partidaActual;
    }
    
}