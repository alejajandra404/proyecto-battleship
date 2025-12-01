/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package mx.itson.utils.enums;

/**
 * Tipos de mensajes para la comunicación
 * 
 * @author alejajandra
 */
public enum TipoMensaje {
    // Registro de jugador
    REGISTRO_JUGADOR,
    REGISTRO_EXITOSO,
    NOMBRE_DUPLICADO,
    
    // Lista de jugadores
    SOLICITAR_JUGADORES,
    LISTA_JUGADORES,
    SIN_JUGADORES_DISPONIBLES,
    JUGADOR_DISPONIBLE,
    
    // Gestión de partida (invitaciones)
    SOLICITAR_PARTIDA, // Cliente A envía al servidor para invitar a B
    INVITACION_RECIBIDA, // Servidor envía a cliente B notificando invitación de A
    ACEPTAR_PARTIDA, // Cliente B acepta la invitación
    RECHAZAR_PARTIDA, // Cliente B rechaza la invitación
    PARTIDA_ACEPTADA, // Servidor notifica a A que B aceptó
    PARTIDA_RECHAZADA, // Servidor notifica a A que B rechazó
    PARTIDA_INICIADA, // Servidor notifica a ambos que la partida comienza
    PARTIDA_CANCELADA, // Servidor notifica que la partida fue cancelada

    // Configuración de naves
    COLOCAR_NAVES, // Cliente envía configuración de sus naves
    NAVES_COLOCADAS, // Servidor confirma que las naves fueron colocadas
    ESPERANDO_OPONENTE_NAVES, // Servidor notifica que está esperando al oponente
    AMBOS_LISTOS, // Servidor notifica que ambos jugadores colocaron naves

    // Juego (disparos y turnos)
    TURNO_INICIADO, // Servidor notifica inicio de turno
    SOLICITAR_DISPARO, // Servidor solicita disparo al jugador en turno
    ENVIAR_DISPARO, // Cliente envía coordenadas de disparo
    RESULTADO_DISPARO, // Servidor envía resultado del disparo
    ACTUALIZAR_TABLEROS, // Servidor envía estado actualizado de tableros
    CAMBIO_TURNO, // Servidor notifica cambio de turno
    TURNO_TIMEOUT, // Servidor notifica que se acabó el tiempo del turno
    ACTUALIZAR_TIEMPO_TURNO, // Servidor envía actualización del tiempo restante del turno

    // Fin de partida
    PARTIDA_GANADA, // Servidor notifica al ganador
    PARTIDA_PERDIDA, // Servidor notifica al perdedor
    PARTIDA_FINALIZADA, // Servidor notifica fin de partida

    // General
    ERROR,
    DESCONEXION
}
