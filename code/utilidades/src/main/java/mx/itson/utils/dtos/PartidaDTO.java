package mx.itson.utils.dtos;

import mx.itson.utils.enums.EstadoPartida;

/**
 *
 * @author PC WHITE WOLF
 */
public class PartidaDTO {
    
    private String idPartida;
    private JugadorDTO jugador1;
    private JugadorDTO jugador2;

    // Tableros
    private TableroDTO tableroJugador1;
    private TableroDTO tableroJugador2;

    // Estado de naves colocadas
    private boolean ambosJugadoresListos;

    // Control de turno
    private String idJugadorEnTurno;
    private int tiempoRestante;
    private EstadoPartida estadoPartida;

    // Ganador
    private String idGanador;

    public PartidaDTO() {}

    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }

    public void setJugador1(JugadorDTO jugador1) {
        this.jugador1 = jugador1;
    }

    public void setJugador2(JugadorDTO jugador2) {
        this.jugador2 = jugador2;
    }

    public void setTableroJugador1(TableroDTO tableroJugador1) {
        this.tableroJugador1 = tableroJugador1;
    }

    public void setTableroJugador2(TableroDTO tableroJugador2) {
        this.tableroJugador2 = tableroJugador2;
    }

    public void setAmbosJugadoresListos(boolean ambosJugadoresListos) {
        this.ambosJugadoresListos = ambosJugadoresListos;
    }

    public void setIdJugadorEnTurno(String idJugadorEnTurno) {
        this.idJugadorEnTurno = idJugadorEnTurno;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public void setEstadoPartida(EstadoPartida estadoPartida) {
        this.estadoPartida = estadoPartida;
    }

    public void setIdGanador(String idGanador) {
        this.idGanador = idGanador;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public JugadorDTO getJugador1() {
        return jugador1;
    }

    public JugadorDTO getJugador2() {
        return jugador2;
    }

    public TableroDTO getTableroJugador1() {
        return tableroJugador1;
    }

    public TableroDTO getTableroJugador2() {
        return tableroJugador2;
    }
    
    public TableroDTO getTableroDeJugador(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return tableroJugador1;
        } else if (idJugador.equals(jugador2.getId())) {
            return tableroJugador2;
        }
        return null;
    }

    public TableroDTO getTableroOponente(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return tableroJugador2;
        } else if (idJugador.equals(jugador2.getId())) {
            return tableroJugador1;
        }
        return null;
    }
    
    public String getNombreJugadorEnTurno() {
        if (idJugadorEnTurno.equals(jugador1.getId())) {
            return jugador1.getNombre();
        } else {
            return jugador2.getNombre();
        }
    }
    
    public boolean isAmbosJugadoresListos() {
        return ambosJugadoresListos;
    }

    public String getIdJugadorEnTurno() {
        return idJugadorEnTurno;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public EstadoPartida getEstadoPartida() {
        return estadoPartida;
    }

    public String getIdGanador() {
        return idGanador;
    }
    
    public String getNombreGanador() {
        if (idGanador == null) {
            return null;
        }
        return idGanador.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre();
    }
    
    public boolean hayGanador() {
        return idGanador != null;
    }

    public JugadorDTO getGanador() {
        if (idGanador == null) {
            return null;
        }
        return idGanador.equals(jugador1.getId()) ? jugador1 : jugador2;
    }
    
    public boolean participaJugador(String idJugador) {
        return idJugador.equals(jugador1.getId()) || idJugador.equals(jugador2.getId());
    }

    public JugadorDTO getOponente(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return jugador2;
        } else if (idJugador.equals(jugador2.getId())) {
            return jugador1;
        }
        return null;
    }
    
}