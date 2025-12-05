package mx.itson.factory;

import mx.itson.exceptions.ModelException;
import mx.itson.models.ITableroDisparos;
import mx.itson.models.ITableroNaves;
import mx.itson.models.TableroDisparosServidor;
import mx.itson.models.TableroNavesServidor;

/**
 * TableroFactory.java
 *
 * Factory para la creación de instancias de tableros.
 * Proporciona métodos para crear tanto tableros de disparos como tableros de naves
 * con el tamaño especificado.
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
public class TableroFactory {

    /**
     * Crea y retorna una nueva instancia de ITableroDisparos.
     * Este tablero registra los disparos realizados hacia el oponente.
     *
     * @param tamanio Tamaño del tablero (número de filas y columnas)
     * @return Nueva instancia de TableroDisparosServidor
     * @throws ModelException Si el tamaño es inválido
     */
    public static ITableroDisparos crearTableroDisparos(int tamanio) throws ModelException{
        return new TableroDisparosServidor(tamanio);
    }

    /**
     * Crea y retorna una nueva instancia de ITableroNaves.
     * Este tablero contiene las naves del jugador y registra los impactos recibidos.
     *
     * @param tamanio Tamaño del tablero (número de filas y columnas)
     * @return Nueva instancia de TableroNavesServidor
     * @throws ModelException Si el tamaño es inválido
     */
    public static ITableroNaves crearTableroNaves(int tamanio) throws ModelException{
        return new TableroNavesServidor(tamanio);
    }
}