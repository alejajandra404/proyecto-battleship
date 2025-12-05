package mx.itson.models;

import java.util.Set;
import mx.itson.exceptions.ModelException;

/**
 * ITableroDisparos.java
 *
 * Interfaz que define el contrato para un tablero de disparos.
 * Este tablero registra todos los disparos realizados por un jugador
 * hacia el oponente durante la partida.
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
public interface ITableroDisparos {

    /**
     * Añade un nuevo disparo al registro del tablero.
     *
     * @param disparo El disparo a registrar
     * @throws ModelException Si el disparo es inválido o ya existe
     */
    public void añadirDisparo(Disparo disparo) throws ModelException;

    /**
     * Valida si un disparo en una coordenada es válido.
     * Verifica que la coordenada esté dentro del tablero y no se haya disparado antes.
     *
     * @param coordenada Coordenada del disparo a validar
     * @return true si el disparo es válido
     * @throws ModelException Si el disparo es inválido
     */
    public boolean validarDisparo(Coordenada coordenada) throws ModelException;

    /**
     * Verifica si ya se ha disparado en una coordenada específica.
     *
     * @param coordenada Coordenada a verificar
     * @return true si ya se disparó en esa coordenada
     * @throws ModelException Si ocurre un error durante la verificación
     */
    public boolean yaDisparado(Coordenada coordenada)  throws ModelException;

    /**
     * Obtiene el conjunto de todos los disparos realizados.
     *
     * @return Set con el historial de disparos
     */
    public Set<Disparo> getDisparosRealizados();
}