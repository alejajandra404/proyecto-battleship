package mx.itson.models;

import java.awt.Color;
import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;

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
    
    private final String idJugador;
    private final String nombre;
    private Color color;
    public boolean enPartida;
    private final ITableroNaves tableroNaves;
    private final ITableroDisparos tableroDisparos;
    
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
    
    @Override
    public String getId() {return idJugador;}
    
    @Override
    public String getNombre() {return this.nombre;}

    @Override
    public Color getColor() {return this.color;}
    
    @Override
    public boolean isEnPartida(){return enPartida;}
    
    @Override
    public ITableroNaves getTableroNaves() {return this.tableroNaves;}
    
    @Override
    public ITableroDisparos getTableroDisparos() {return this.tableroDisparos;}
    
    public void setColor(Color color) {this.color = color;}

    public void setEnPartida(boolean enPartida) {this.enPartida = enPartida;}
    
    @Override
    public void marcarDisparo(Disparo disparo) throws ModelException {this.tableroDisparos.añadirDisparo(disparo);}
    
    @Override
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException {return this.tableroNaves.recibirImpacto(coordenada);}
    
    @Override
    public boolean validarDisparo(Coordenada coordenada) throws ModelException {return this.tableroDisparos.validarDisparo(coordenada);}

    @Override
    public boolean añadirNave(Nave nave) throws ModelException {return this.tableroNaves.añadirNave(nave);}

    @Override
    public boolean eliminarNave(Nave nave) throws ModelException {return this.tableroNaves.eliminarNave(nave);}

    @Override
    public boolean colocarNaves(List<Nave> naves) throws ModelException {return this.tableroNaves.colocarNaves(naves);}
}