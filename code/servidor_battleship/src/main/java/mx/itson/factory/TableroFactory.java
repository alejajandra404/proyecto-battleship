package mx.itson.factory;

import mx.itson.exceptions.ModelException;
import mx.itson.models.ITableroDisparos;
import mx.itson.models.ITableroNaves;
import mx.itson.models.TableroDisparosServidor;
import mx.itson.models.TableroNavesServidor;

/**
 *
 * @author PC WHITE WOLF
 */
public class TableroFactory {
    
    public static ITableroDisparos crearTableroDisparos(int tamanio) throws ModelException{
        return new TableroDisparosServidor(tamanio);
    }
    
    public static ITableroNaves crearTableroNaves(int tamanio, int totalNaves) throws ModelException{
        return new TableroNavesServidor(tamanio, totalNaves);
    }
}