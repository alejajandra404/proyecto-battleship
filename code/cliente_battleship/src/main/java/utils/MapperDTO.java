/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import dtos.*;
import enums.*;
import exceptions.NaveException;
import models.*;

/**
 * DTOMapper - Clase utilitaria para convertir entre entidades del modelo y DTOs
 *
 * Patrón: Mapper/Converter
 *
 * Responsabilidades: 
 * - Convertir objetos del modelo a DTOs (para enviar) 
 * - Convertir DTOs a objetos del modelo (para recibir) 
 * - Mantener el desacoplamiento entre capas
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class MapperDTO {

    // COORDENADA
    /**
     * Convierte Coordenada del modelo a CoordenadaDTO
     *
     * @param coordenada
     * @return
     */
    public static CoordenadaDTO toDTO(Coordenada coordenada) {
        if (coordenada == null) {
            return null;
        }
        return new CoordenadaDTO(coordenada.obtenerX(), coordenada.obtenerY());
    }

    /**
     * Convierte CoordenadaDTO a Coordenada del modelo
     *
     * @param dto
     * @return
     */
    public static Coordenada toEntity(CoordenadaDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Coordenada(dto.getX(), dto.getY());
    }

    // DISPARO 
    /**
     * Convierte Disparo del modelo a DisparoDTO
     *
     * @param disparo
     * @return
     */
    public static DisparoDTO toDTO(Disparo disparo) {
        if (disparo == null) {
            return null;
        }

        CoordenadaDTO coordDTO = toDTO(disparo.obtenerCoordenada());
        String nombreJugador = disparo.getJugador() != null
                ? disparo.getJugador().getNombre() : "Desconocido";

        DisparoDTO dto = new DisparoDTO(
                coordDTO,
                disparo.obtenerResultado(),
                nombreJugador,
                disparo.procesarDisparo()
        );

        dto.setTimestamp(disparo.getTimestamp());

        return dto;
    }

    /**
     * Convierte DisparoDTO a Disparo del modelo Nota: El jugador debe ser
     * asignado posteriormente
     *
     * @param dto
     * @param jugador
     * @return
     */
    public static Disparo toEntity(DisparoDTO dto, Jugador jugador) {
        if (dto == null) {
            return null;
        }

        Coordenada coordenada = toEntity(dto.getCoordenada());
        ResultadoDisparo resultado = dto.getResultado();

        return new Disparo(coordenada, resultado, jugador);
    }

    // NAVE 
    /**
     * Convierte Nave del modelo a NaveDTO
     *
     * @param nave
     * @return
     */
    public static NaveDTO toDTO(Nave nave) {
        if (nave == null) {
            return null;
        }

        Coordenada[] coordenadas = nave.obtenerCoordenadas();
        CoordenadaDTO[] coordenadasDTO = new CoordenadaDTO[coordenadas.length];

        for (int i = 0; i < coordenadas.length; i++) {
            coordenadasDTO[i] = toDTO(coordenadas[i]);
        }

        return new NaveDTO(
                nave.getTipo(),
                nave.verificarEstado(),
                nave.obtenerOrientacion(),
                coordenadasDTO,
                nave.getImpactosRecibidos()
        );
    }

    /**
     * Convierte NaveDTO a Nave del modelo
     *
     * @param dto
     * @return
     * @throws NaveException
     */
    public static Nave toEntity(NaveDTO dto) throws NaveException {
        if (dto == null) {
            return null;
        }

        CoordenadaDTO[] coordenadasDTO = dto.getCoordenadas();
        Coordenada[] coordenadas = new Coordenada[coordenadasDTO.length];

        for (int i = 0; i < coordenadasDTO.length; i++) {
            coordenadas[i] = toEntity(coordenadasDTO[i]);
        }

        return new Nave(
                dto.getTipo(),
                dto.getOrientacion(),
                coordenadas
        );
    }

    // TURNO 
    /**
     * Crea un TurnoDTO desde la información de la partida
     *
     * @param partida
     * @param tiempoRestante
     * @param mensaje
     * @return
     */
    public static TurnoDTO crearTurnoDTO(Partida partida, int tiempoRestante, String mensaje) {
        if (partida == null) {
            return null;
        }

        String nombreJugadorTurno = partida.obtenerJugadorTurno().getNombre();
        boolean partidaEnCurso = !partida.verificarFinPartida();

        return new TurnoDTO(
                nombreJugadorTurno,
                tiempoRestante,
                partidaEnCurso,
                mensaje
        );
    }

    // ESTADO PARTIDA
    /**
     * Crea un EstadoPartidaDTO desde la información de la partida
     *
     * @param partida
     * @param tiempoRestante
     * @return
     */
    public static EstadoPartidaDTO crearEstadoPartidaDTO(Partida partida, int tiempoRestante) {

        if (partida == null) {
            return null;
        }

        EstadoPartidaDTO dto = new EstadoPartidaDTO(
                partida.getJugador1().getNombre(),
                partida.getJugador2().getNombre(),
                partida.obtenerJugadorTurno().getNombre(),
                partida.getEstado(),
                tiempoRestante
        );

        // Si la partida terminó, obtener ganador
        if (partida.verificarFinPartida()) {
            Jugador ganador = partida.determinarGanador();
            if (ganador != null) {
                dto.setGanador(ganador.getNombre());
            }
        }

        return dto;
    }

    // CONVERSIÓN DE ARRAYS 
    /**
     * Convierte un array de Naves a array de NaveDTOs
     *
     * @param naves
     * @return
     */
    public static NaveDTO[] toDTO(Nave[] naves) {
        if (naves == null) {
            return null;
        }

        NaveDTO[] dtos = new NaveDTO[naves.length];
        for (int i = 0; i < naves.length; i++) {
            dtos[i] = toDTO(naves[i]);
        }

        return dtos;
    }

    /**
     * Convierte un array de NaveDTOs a array de Naves
     *
     * @param dtos
     * @return
     * @throws NaveException
     */
    public static Nave[] toEntity(NaveDTO[] dtos) throws NaveException {
        if (dtos == null) {
            return null;
        }

        Nave[] naves = new Nave[dtos.length];
        for (int i = 0; i < dtos.length; i++) {
            naves[i] = toEntity(dtos[i]);
        }

        return naves;
    }

}
