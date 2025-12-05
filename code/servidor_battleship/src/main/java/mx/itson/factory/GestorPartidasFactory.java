package mx.itson.factory;

import mx.itson.subsistema_gestor_partidas.GestorPartidas;
import mx.itson.subsistema_gestor_partidas.IGestorPartidas;

/**
 * GestorPartidasFactory.java
 *
 * Factory para la creación de instancias de IGestorPartidas.
 * Proporciona un método estático para obtener un gestor de partidas
 * que administra el conjunto de partidas activas en el servidor.
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
public class GestorPartidasFactory {

    /**
     * Crea y retorna una nueva instancia de IGestorPartidas.
     *
     * @return Nueva instancia de GestorPartidas
     */
    public static IGestorPartidas crearGestorPartidas(){
        return new GestorPartidas();
    }
}