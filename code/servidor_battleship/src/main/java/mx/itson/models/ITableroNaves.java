package mx.itson.models;

import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;

/**
 * ITableroNaves.java
 *
 * Interfaz que define el contrato para un tablero de naves.
 * Este tablero contiene las naves del jugador y gestiona los impactos
 * recibidos del oponente.
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
public interface ITableroNaves {

    /**
     * Añade una nave al tablero.
     *
     * @param nave Nave a añadir
     * @return true si se añadió exitosamente
     * @throws ModelException Si la nave es inválida o la posición está ocupada
     */
    public boolean añadirNave(Nave nave) throws ModelException;

    /**
     * Elimina una nave del tablero.
     *
     * @param nave Nave a eliminar
     * @return true si se eliminó exitosamente
     * @throws ModelException Si la nave no existe o es inválida
     */
    public boolean eliminarNave(Nave nave) throws ModelException;

    /**
     * Coloca todas las naves en el tablero.
     * Valida que se coloquen todas las naves requeridas.
     *
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente todas las naves
     * @throws ModelException Si faltan naves o hay errores en la colocación
     */
    public boolean colocarNaves(List<Nave> naves) throws ModelException;

    /**
     * Busca y retorna la nave ubicada en una coordenada específica.
     *
     * @param coordenada Coordenada a buscar
     * @return La nave en esa coordenada, o null si no hay ninguna
     */
    public Nave encontrarNaveEnCoordenada(Coordenada coordenada);

    /**
     * Recibe un impacto en una coordenada específica.
     * Actualiza el estado de la casilla y la nave si es impactada.
     *
     * @param coordenada Coordenada del impacto
     * @return Estado de la casilla después del impacto
     * @throws ModelException Si ocurre un error al procesar el impacto
     */
    public EstadoCasilla recibirImpacto(Coordenada coordenada) throws ModelException;

    /**
     * Verifica si todas las naves han sido colocadas en el tablero.
     *
     * @return true si todas las naves están colocadas
     */
    public boolean navesColocadas();

    /**
     * Obtiene el número de naves hundidas.
     *
     * @return Cantidad de naves hundidas
     */
    public int getNavesHundidas();

    /**
     * Obtiene la lista de todas las naves en el tablero.
     *
     * @return Lista de naves
     */
    public List<Nave> getNaves();

    /**
     * Obtiene la matriz de casillas del tablero.
     *
     * @return Matriz bidimensional de casillas
     */
    public Casilla[][] getCasillas();

    /**
     * Obtiene el número total de naves que debe tener el tablero.
     *
     * @return Total de naves requeridas
     */
    public int getTotalNaves();

    /**
     * Verifica si todas las naves del tablero han sido hundidas.
     *
     * @return true si todas las naves están hundidas
     */
    public boolean todasNavesHundidas();
}