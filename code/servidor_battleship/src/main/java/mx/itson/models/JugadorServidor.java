package mx.itson.models;

import java.util.Set;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.ResultadoDisparo;
import mx.itson.utils.dtos.EstadisticaDTO;

/**
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
    private final String nombre;
    private String color;
    private final ITableroNaves tableroNaves;
    private final ITableroDisparos tableroDisparos;

    public JugadorServidor(String nombre, ITableroNaves tableroNaves, ITableroDisparos tableroDisparos) {
        this.nombre = nombre;
        this.tableroNaves = tableroNaves;
        this.tableroDisparos = tableroDisparos;
    }
    
    /**
     * Devuelve el nombre del jugador
     * @return El nombre del jugador
     */
    public String getNombre() {return this.nombre;}

    /**
     * Devuelve el color asignado al jugador
     * @return El color del jugador
     */
    public String getColor() {return this.color;}
    
    /**
     * Obtiene el tablero de naves del jugador 
     * 
     * @return 
     */
    public ITableroNaves getTableroNaves() {return this.tableroNaves;}

    /**
     * Obtiene el tablero de disparos del jugador 
     * 
     * @return 
     */
    public ITableroDisparos getTableroDisparos() {return this.tableroDisparos;}
    
    @Override
    public void marcarDisparo(Disparo disparo) throws ModelException {this.tableroDisparos.añadirDisparo(disparo);}
    
    @Override
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException {return this.tableroNaves.recibirImpacto(coordenada);}
    
    @Override
    public boolean validarDisparo(Disparo disparo) throws ModelException {return this.tableroDisparos.validarDisparo(disparo);}

    @Override
    public boolean añadirNave(Nave nave) throws ModelException {return this.tableroNaves.añadirNave(nave);}

    @Override
    public boolean eliminarNave(Coordenada[] coordenadas) throws ModelException {return this.tableroNaves.eliminarNave(coordenadas);}
    
    /**
     * Genera el DTO de estadísticas calculando los aciertos y totales basándose en el 
     * historial del tablero de disparos
     * @param esGanador true si este jugador ganó la partida
     * @param barcosHundidos Cantidad de barcos que este jugador hundió (se recibe externamente)
     * @return Un objeto EstadisticasDTO listo para enviar al cliente
     */
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