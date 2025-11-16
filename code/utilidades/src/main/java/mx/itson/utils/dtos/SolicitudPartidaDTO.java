package mx.itson.utils.dtos;

import java.io.Serializable;

/**
 * DTO para solicitudes de partida (invitaciones)
 * Contiene información del solicitante y del invitado
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class SolicitudPartidaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idSolicitante;
    private String nombreSolicitante;
    private String idInvitado;
    private String nombreInvitado;
    private long timestamp;

    /**
     * Constructor por defecto
     */
    public SolicitudPartidaDTO() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor completo
     * @param idSolicitante ID del jugador que envía la invitación
     * @param nombreSolicitante Nombre del solicitante
     * @param idInvitado ID del jugador invitado
     * @param nombreInvitado Nombre del invitado
     */
    public SolicitudPartidaDTO(String idSolicitante, String nombreSolicitante,
                               String idInvitado, String nombreInvitado) {
        this.idSolicitante = idSolicitante;
        this.nombreSolicitante = nombreSolicitante;
        this.idInvitado = idInvitado;
        this.nombreInvitado = nombreInvitado;
        this.timestamp = System.currentTimeMillis();
    }

    public String getIdSolicitante() {
        return idSolicitante;
    }

    public void setIdSolicitante(String idSolicitante) {
        this.idSolicitante = idSolicitante;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getIdInvitado() {
        return idInvitado;
    }

    public void setIdInvitado(String idInvitado) {
        this.idInvitado = idInvitado;
    }

    public String getNombreInvitado() {
        return nombreInvitado;
    }

    public void setNombreInvitado(String nombreInvitado) {
        this.nombreInvitado = nombreInvitado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SolicitudPartidaDTO{" +
                "solicitante='" + nombreSolicitante + '\'' +
                ", invitado='" + nombreInvitado + '\'' +
                '}';
    }
}
