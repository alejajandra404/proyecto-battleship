package mx.itson.models;

import java.util.HashSet;
import java.util.Set;
import mx.itson.exceptions.ModelException;

/**
 * TableroDisparosServidor.java
 *
 * Clase que representa el tablero de disparos de un jugador en el servidor.
 * Registra todos los disparos realizados por el jugador hacia el oponente
 * y valida que no se repitan disparos en la misma coordenada.
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
public class TableroDisparosServidor extends Tablero implements ITableroDisparos {
    
    private final Set<Disparo> disparos;

    /**
     * Constructor de la clase TableroDisparosServidor.
     * Inicializa el tablero con el tamaño especificado y crea el conjunto de disparos.
     *
     * @param tamanio Tamaño del tablero
     * @throws ModelException Si el tamaño es inválido
     */
    public TableroDisparosServidor(int tamanio) throws ModelException {
        super(tamanio);
        this.disparos = new HashSet<>();
    }

    /**
     * Añade un nuevo disparo al registro del tablero.
     * Valida que el disparo sea válido antes de agregarlo.
     *
     * @param disparo El disparo a registrar
     * @throws ModelException Si el disparo es null o inválido
     */
    @Override
    public void añadirDisparo(Disparo disparo) throws ModelException{
        // Verifica que el disparo no sea null
        if(disparo == null) throw new ModelException("Disparo inválido");
        // Valida el disparo (podría ser innecesario si es validado por la partida o el subsistema)
        validarDisparo(disparo.obtenerCoordenada());
        // Añade el disparo
        this.disparos.add(disparo);
    }

    /**
     * Valida si un disparo en una coordenada es válido.
     * Verifica que la coordenada esté dentro del tablero y no se haya disparado antes.
     *
     * @param coordenada Coordenada del disparo a validar
     * @return true si el disparo es válido
     * @throws ModelException Si la coordenada es null, está fuera del tablero o ya fue disparada
     */
    @Override
    public boolean validarDisparo(Coordenada coordenada) throws ModelException {
        // Si el disparo no existe
        if(coordenada == null) throw new ModelException("Disparo inválido.");
        
        // Si la coordenada está fuera de los límites del tablero
        if(!super.validarCoordenada(coordenada)) throw new ModelException("Coordenada del disparo fuera de los límites del tablero");
        
        // Verifica las coordenadas de cada disparo
        for (Disparo d : disparos) 
            if (d.obtenerCoordenada().equals(coordenada)) throw new ModelException("Ya existe otro disparo con la misma coordenada.");
        
        // Verdadero si pasa todas las validaciones
        return true;
    }

    /**
     * Verifica si ya se ha disparado en una coordenada específica.
     *
     * @param coordenada Coordenada a verificar
     * @return true si ya se disparó en esa coordenada, false en caso contrario
     */
    @Override
    public boolean yaDisparado(Coordenada coordenada) {
        for (Disparo disparo : disparos)
            if (disparo.obtenerCoordenada().equals(coordenada)) return true;
        return false;
    }

    /**
     * Obtiene el conjunto de todos los disparos realizados.
     *
     * @return Set con el historial de disparos
     */
    @Override
    public Set<Disparo> getDisparosRealizados() {
        return this.disparos;
    }
}