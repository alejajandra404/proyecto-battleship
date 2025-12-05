package mx.itson.subsistema_gestor_partidas;

import mx.itson.utils.dtos.JugadorDTO;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import mx.itson.exceptions.GestorPartidasException;
import mx.itson.exceptions.ModelException;
import mx.itson.factory.JugadorFactory;
import mx.itson.factory.PartidaFactory;
import mx.itson.mappers.NaveMapper;
import mx.itson.mappers.PartidaMapper;
import mx.itson.models.IPartida;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.NaveDTO;
import mx.itson.utils.dtos.PartidaDTO;

/**
 * Gestor centralizado de partidas activas en el servidor
 * Thread-safe para manejo concurrente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class GestorPartidas implements IGestorPartidas{

    private final Map<String, IPartida> partidas; // Key: ID de partida
    private final Map<String, String> jugadorAPartida; // Key: ID de jugador, Value: ID de partida

    /**
     * Constructor
     */
    public GestorPartidas() {
        this.partidas = new ConcurrentHashMap<>();
        this.jugadorAPartida = new ConcurrentHashMap<>();
    }

    /**
     * Crea una nueva partida entre dos jugadores
     *
     * @param jugador1
     * @param jugador2
     * @return La partida creada
     * @throws mx.itson.exceptions.GestorPartidasException
     */
    @Override
    public synchronized PartidaDTO crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) throws GestorPartidasException {
        
        try {
            IPartida partidaNueva = PartidaFactory.crearPartida(
                    UUID.randomUUID().toString(), // Se crea un ID al azar
                    JugadorFactory.crearJugador(jugador1.getId(), jugador1.getNombre(), jugador1.getColor().toString()), // Se crea jugador 1 en el modelo
                    JugadorFactory.crearJugador(jugador2.getId(), jugador2.getNombre(), jugador2.getColor().toString()) // Se crea jugador 2 en el modelo
            );
            
//            Partida partida = new Partida(jugador1, jugador2);
            
            partidas.put(partidaNueva.getIdPartida(), partidaNueva);
            jugadorAPartida.put(jugador1.getId(), partidaNueva.getIdPartida());
            jugadorAPartida.put(jugador2.getId(), partidaNueva.getIdPartida());
            
            System.out.println("[GESTOR_PARTIDAS] Partida creada: " + partidaNueva.getIdPartida());
            
            PartidaDTO partida = PartidaMapper.toDTO(partidaNueva);
            
            return partida;
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }

    /**
     * Obtiene la partida de un jugador
     *
     * @param idJugador ID del jugador
     * @return La partida del jugador o null si no está en ninguna
     * @throws mx.itson.exceptions.GestorPartidasException
     */
    @Override
    public synchronized PartidaDTO obtenerPartidaDeJugador(String idJugador) throws GestorPartidasException{
        String idPartida = jugadorAPartida.get(idJugador);
        try {
            return (idPartida == null) ? null : PartidaMapper.toDTO(partidas.get(idPartida));
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }

    /**
     * Obtiene una partida por su ID
     *
     * @param idPartida ID de la partida
     * @return La partida o null si no existe
     * @throws mx.itson.exceptions.GestorPartidasException
     */
    @Override
    public synchronized PartidaDTO obtenerPartida(String idPartida) throws GestorPartidasException{
        try {
            IPartida partida = partidas.get(idPartida);
            return (partida != null) ? PartidaMapper.toDTO(partida) : null;
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }

    /**
     * Obtiene la partida del modelo (IPartida) sin convertir a DTO
     *
     * @param idPartida ID de la partida
     * @return La partida del modelo o null si no existe
     */
    @Override
    public synchronized IPartida obtenerPartidaModelo(String idPartida) {
        return partidas.get(idPartida);
    }

    /**
     * Verifica si un jugador tiene una partida activa.
     *
     * @param idJugador ID del jugador
     * @return true si el jugador está en una partida activa
     */
    @Override
    public boolean verificarJugadorPartidaActiva(String idJugador){return (jugadorAPartida.get(idJugador) != null);}
    
    /**
     * Elimina una partida del gestor
     *
     * @param idPartida ID de la partida
     */
    @Override
    public synchronized void eliminarPartida(String idPartida) {
        IPartida partida = partidas.remove(idPartida);
        if (partida != null) {
            jugadorAPartida.remove(partida.getJugador1().getId());
            jugadorAPartida.remove(partida.getJugador2().getId());
            System.out.println("[GESTOR_PARTIDAS] Partida eliminada: " + idPartida);
        }
    }

    /**
     * Verifica si un jugador está en una partida activa
     *
     * @param idJugador ID del jugador
     * @return true si está en una partida
     */
    @Override
    public synchronized boolean jugadorEnPartida(String idJugador) {return jugadorAPartida.containsKey(idJugador);}

    /**
     * Obtiene el número de partidas activas
     *
     * @return Número de partidas
     */
    @Override
    public synchronized int cantidadPartidas() {return partidas.size();}

    /**
     * Obtiene todas las partidas activas
     *
     * @return Lista de partidas
     * @throws mx.itson.exceptions.GestorPartidasException
     */
    @Override
    public synchronized List<PartidaDTO> obtenerTodasPartidas() throws GestorPartidasException {
        List<IPartida> partidasActuales = new ArrayList(this.partidas.values());
        try {
            return (partidasActuales.isEmpty()) ? new ArrayList<>() : PartidaMapper.toDTOList(partidasActuales);
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }

    /**
     * Coloca las naves de un jugador en el tablero de una partida.
     * Si ambos jugadores ya colocaron sus naves, inicia el juego automáticamente.
     *
     * @param idPartida ID de la partida
     * @param idJugador ID del jugador que coloca las naves
     * @param naves Lista de naves a colocar
     * @return DTO de la partida actualizada, o null si no se pudieron colocar
     * @throws GestorPartidasException Si ocurre un error al colocar las naves
     */
    @Override
    public PartidaDTO colocarNaves(String idPartida, String idJugador, List<NaveDTO> naves) throws GestorPartidasException {
        try {
            IPartida partida = partidas.get(idPartida);
            return (partida.colocarNaves(idJugador, NaveMapper.toEntityList(naves))) ?
                    PartidaMapper.toDTO(partida) :
                    null;
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Establece el callback para notificar cuando se agota el tiempo de un turno.
     *
     * @param idPartida ID de la partida
     * @param callback Consumer que recibe el ID del jugador que perdió el turno
     */
    @Override
    public void establecerRespuestaTiempoAgotado(String idPartida, Consumer<String> callback){
        IPartida partida = partidas.get(idPartida);
        if(partida != null) partida.establecerRespuestaTiempoAgotado(callback);
    }

    /**
     * Establece el callback para actualizaciones periódicas del tiempo restante del turno.
     *
     * @param idPartida ID de la partida
     * @param callback Consumer que recibe el tiempo restante en segundos
     */
    @Override
    public void establecerCallbackActualizacionTiempo(String idPartida, Consumer<Integer> callback){
        IPartida partida = partidas.get(idPartida);
        if(partida != null) partida.establecerCallbackActualizacionTiempo(callback);
    }

    /**
     * Inicia el temporizador del turno actual de una partida.
     *
     * @param idPartida ID de la partida
     */
    @Override
    public void iniciarTemporizador(String idPartida) {partidas.get(idPartida).iniciarTemporizador();}
    
    /**
     * Libera los recursos asociados a una partida (temporizadores, threads, etc.).
     *
     * @param idPartida ID de la partida
     */
    @Override
    public void liberarRecursos(String idPartida){
        IPartida partida = partidas.get(idPartida);
        if(partida != null) partida.liberarRecursos();
    }
    
    @Override
    public DisparoDTO procesarDisparo(String idPartida, String idJugador, CoordenadaDTO coordenada) throws GestorPartidasException {
        try {
            IPartida partida = partidas.get(idPartida);
            
            // Valida que exista la partida (colocado aquí en caso de que se reutilice el subsistema)
            if(partida == null)return null;
            
            // Verifica que el jugador tenga el turno
            if(!partida.verificarJugadorTurno(idJugador))throw new GestorPartidasException("No es tu turno.");
            
            // Verifica que el disparo sea válido (Técnicamente nunca da false en la implementación del método)
            if(!partida.validarDisparo(idJugador, coordenada))throw new GestorPartidasException("Disparo inválido.");
            
            // Procesa el disparo y regresa el resultado
            return partida.procesarDisparo(idJugador, coordenada);
            
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            throw new GestorPartidasException(ex.getMessage(), ex);
        }
    }
}