package mx.itson.models;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.dtos.EstadisticaDTO;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.ResultadoDisparo;

/**
 * JugadorServidor.java
 *
 * Clase que representa a un jugador en el servidor.
 * Gestiona la información del jugador, sus tableros (naves y disparos),
 * y delega las operaciones a los tableros correspondientes.
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
 */
public class JugadorServidor implements IJugador{
    
    private final String idJugador;
    private final String nombre;
    private Color color;
    public boolean enPartida;
    private final ITableroNaves tableroNaves;
    private final ITableroDisparos tableroDisparos;

    /**
     * Constructor de la clase JugadorServidor.
     * Inicializa el jugador con sus tableros y datos básicos.
     *
     * @param idJugador ID único del jugador
     * @param nombre Nombre del jugador
     * @param color Color asignado al jugador (no utilizado en esta versión)
     * @param tableroNaves Tablero de naves del jugador
     * @param tableroDisparos Tablero de disparos del jugador
     */
    public JugadorServidor(
            String idJugador,
            String nombre,
            String color,
            ITableroNaves tableroNaves,
            ITableroDisparos tableroDisparos
    ) {
        this.idJugador = idJugador;
        this.nombre = nombre;
        this.tableroNaves = tableroNaves;
        this.tableroDisparos = tableroDisparos;
    }
    
    /**
     * Obtiene el ID único del jugador.
     *
     * @return ID del jugador
     */
    @Override
    public String getId() {return idJugador;}

    /**
     * Obtiene el nombre del jugador.
     *
     * @return Nombre del jugador
     */
    @Override
    public String getNombre() {return this.nombre;}

    /**
     * Obtiene el color asignado al jugador.
     *
     * @return Color del jugador
     */
    @Override
    public Color getColor() {return this.color;}

    /**
     * Verifica si el jugador está actualmente en una partida.
     *
     * @return true si el jugador está en partida
     */
    @Override
    public boolean isEnPartida(){return enPartida;}

    /**
     * Obtiene el tablero de naves del jugador.
     *
     * @return Tablero de naves
     */
    @Override
    public ITableroNaves getTableroNaves() {return this.tableroNaves;}

    /**
     * Obtiene el tablero de disparos del jugador.
     *
     * @return Tablero de disparos
     */
    @Override
    public ITableroDisparos getTableroDisparos() {return this.tableroDisparos;}

    /**
     * Establece el color del jugador.
     *
     * @param color Nuevo color del jugador
     */
    public void setColor(Color color) {this.color = color;}

    /**
     * Establece si el jugador está en partida.
     *
     * @param enPartida true si el jugador está en partida
     */
    public void setEnPartida(boolean enPartida) {this.enPartida = enPartida;}

    /**
     * Marca un disparo realizado por este jugador en su tablero de disparos.
     *
     * @param disparo El objeto Disparo que contiene el resultado y la coordenada
     * @throws ModelException Si ocurre un error al marcar el disparo
     */
    @Override
    public void marcarDisparo(Disparo disparo) throws ModelException {this.tableroDisparos.añadirDisparo(disparo);}

    /**
     * Procesa un disparo recibido de un oponente en una coordenada específica.
     *
     * @param coordenada La coordenada donde el oponente ha disparado
     * @return Estado de la casilla después del impacto
     * @throws ModelException Si ocurre un error al procesar el disparo
     */
    @Override
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException {return this.tableroNaves.recibirImpacto(coordenada);}

    /**
     * Valida si un disparo en una coordenada es válido.
     *
     * @param coordenada Coordenada a validar
     * @return true si el disparo es válido
     * @throws ModelException Si ocurre un error durante la validación
     */
    @Override
    public boolean validarDisparo(Coordenada coordenada) throws ModelException {return this.tableroDisparos.validarDisparo(coordenada);}

    /**
     * Añade una nave al tablero del jugador.
     *
     * @param nave Nave a añadir
     * @return true si se añadió exitosamente
     * @throws ModelException Si ocurre un error al añadir la nave
     */
    @Override
    public boolean añadirNave(Nave nave) throws ModelException {return this.tableroNaves.añadirNave(nave);}

    /**
     * Elimina una nave del tablero del jugador.
     *
     * @param nave Nave a eliminar
     * @return true si se eliminó exitosamente
     * @throws ModelException Si ocurre un error al eliminar la nave
     */
    @Override
    public boolean eliminarNave(Nave nave) throws ModelException {return this.tableroNaves.eliminarNave(nave);}

    /**
     * Coloca todas las naves del jugador en el tablero.
     *
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente todas las naves
     * @throws ModelException Si ocurre un error al colocar las naves
     */
    @Override
    public boolean colocarNaves(List<Nave> naves) throws ModelException {return this.tableroNaves.colocarNaves(naves);}

    /**
     * Obtiene el jugador del modelo por su ID.
     * Este método no está soportado en JugadorServidor.
     *
     * @param idBusqueda ID del jugador a buscar
     * @return No implementado, lanza UnsupportedOperationException
     */
    @Override
    public IJugador getJugadorModeloPorId(String idBusqueda) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Genera las estadísticas finales del jugador al terminar la partida.
     * Calcula los aciertos y totales basándose en el historial del tablero de disparos.
     *
     * @param esGanador true si este jugador ganó la partida
     * @param barcosHundidos Cantidad de barcos que este jugador hundió
     * @return DTO con las estadísticas del jugador
     */
    @Override
    public EstadisticaDTO generarEstadisticas(boolean esGanador, int barcosHundidos) {
        //Se obtiene el historial de disparos del tablero
        Set<Disparo> historialDisparos = this.tableroDisparos.getDisparosRealizados();
        
        int totalDisparos = historialDisparos.size();
        int aciertos = 0;

        //Se verifica entre todo el historial aquellos disparos que su resultado sea diferente de "AGUA"
        for (Disparo d : historialDisparos) {
            if (d.obtenerResultado() != ResultadoDisparo.AGUA) {
                aciertos++;
            }
        }
        
        //Se crea y se retorna el DTO con la nueva información
        return new EstadisticaDTO(
                this.nombre, esGanador, totalDisparos, aciertos, barcosHundidos);
    }
}