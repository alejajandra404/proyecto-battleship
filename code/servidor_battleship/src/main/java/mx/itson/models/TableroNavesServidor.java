package mx.itson.models;

import java.util.HashSet;
import java.util.Set;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;

/**
 *
 * Clase entidad que hereda de la clase padre Tablero
 * y representa el tablero de naves de un jugador.
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
public class TableroNavesServidor extends Tablero implements ITableroNaves {
    
    private Set<Nave> naves;
    private final Casilla[][] casillas;
    
    public TableroNavesServidor(int tamanio) throws ModelException {
        super(tamanio);
        this.casillas = new Casilla[tamanio][tamanio];
        this.naves = new HashSet<>();
    }
    
    public TableroNavesServidor(int tamanio, Set<Nave> naves) throws ModelException {
        super(tamanio);
        this.naves = naves;
        this.casillas = new Casilla[tamanio][tamanio];
    }
    
    @Override
    public boolean añadirNave(Nave nave) throws ModelException {
        Coordenada[] coordenadasBarcos = nave.obtenerCoordenadas();
        // Verifica si alguna de las casillas ya está ocupada (para cada coordenada que abarca la nave)
        for(Coordenada coordenada : coordenadasBarcos){
            if(casillas[coordenada.obtenerX()][coordenada.obtenerY()].estaOcupada())
                throw new ModelException("Ya se encuentra una nave en la posición.");
        }
        // Actualiza el estado de cada casilla como ocupada
        for(Coordenada coordenada : coordenadasBarcos)
            casillas[coordenada.obtenerX()][coordenada.obtenerY()].ocuparCasilla();
        
        // Se agrega la nave
        naves.add(nave);
        // Se regresa true
        return true;
    }

    @Override
    public boolean eliminarNave(Coordenada[] coordenadas) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public EstadoCasilla recibirImpacto(Coordenada coordenada) throws ModelException {
        // Se verifica si la casilla, ubicada en la coordenada recibida, ya fue impactada previamente
        if(casillas[coordenada.obtenerX()][coordenada.obtenerY()].estaImpactada())
            throw new ModelException("La casilla ya ha sido impactada previamente");
        
        // Recorre las naves y verifica si alguna contiene la coordenada recibida
        for(Nave nave: naves){
            // Si alguna nave contiene la coordenada, esta recibe el impacto (actualiza su estado)
            if(nave.recibirImpacto(coordenada)){
                // Se verifica si la nave está hundida
                if(nave.estaHundida()){
                    // Si es así, se actualiza el estado de todas las casillas ocupadas por la nave como IMPACTADA_HUNDIDA
                    for(Coordenada coordenadaNave : nave.obtenerCoordenadas()){
                        Casilla casillaActual = casillas[coordenadaNave.obtenerX()][coordenadaNave.obtenerY()];
                        // Solo marcar como hundida si estaba averiada
                        if (casillaActual.obtenerEstado() == EstadoCasilla.IMPACTADA_AVERIADA) {
                            casillaActual.marcarImpactoHundida();
                        } else if (casillaActual.obtenerEstado() == EstadoCasilla.OCUPADA) {
                            // Si es el primer impacto que hunde la nave, marcar directamente
                            casillaActual.marcarImpactoAveriada();
                            casillaActual.marcarImpactoHundida();
                        }
                    }
                    // Se regresa el estado IMPACTADA_HUNDIDA
                    return EstadoCasilla.IMPACTADA_HUNDIDA;
                // Si la nave solo fue averiada, se actualiza el estado de la casilla como IMPACTADA_AVERIADA
                } else{
                    casillas[coordenada.obtenerX()][coordenada.obtenerY()].marcarImpactoAveriada();
                    // Se regresa el estado IMPACTADA_AVERIADA
                    return EstadoCasilla.IMPACTADA_AVERIADA;
                }
            }
        }
        // Si ninguna nave fue impactada, marcar la casilla como agua
        casillas[coordenada.obtenerX()][coordenada.obtenerY()].marcarImpactoAgua();
        
        // Si ninguna nave fue impactada, se regresa el estado IMPACTADA_VACIA
        return EstadoCasilla.IMPACTADA_VACIA;
    }
    
    /**
     * Inicializa la matriz de casillas vacías 
     */
    public void inicializarCasillas() {
        for (int i = 0; i < getTamanio(); i++) {
            for (int j = 0; j < getTamanio(); j++) {
                casillas[i][j] = new Casilla(new Coordenada(i, j));
            }
        }
    }

    @Override
    public boolean todasLasNavesHundidas() {
        return false;
    }
    
}