package mx.itson.factory;

import mx.itson.subsistema_gestor_partidas.GestorPartidas;
import mx.itson.subsistema_gestor_partidas.IGestorPartidas;

/**
 *
 * @author PC WHITE WOLF
 */
public class GestorPartidasFactory {public static IGestorPartidas crearGestorPartidas(){return new GestorPartidas();}}