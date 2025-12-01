package mx.itson.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.TipoNave;

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
    private int navesHundidas;
    private final int totalNaves;
    
    public TableroNavesServidor(int tamanio, int totalNaves) throws ModelException {
        super(tamanio);
        this.totalNaves = totalNaves;
        this.casillas = new Casilla[tamanio][tamanio];
        this.naves = new HashSet<>();
        inicializarCasillas();
    }
    
    @Override
    public boolean añadirNave(Nave nave) throws ModelException {
        // Verifica que la coordenada exista y tenga coordenadas
        if(nave == null || nave.obtenerCoordenadas() == null || nave.getTipo() == null)
            throw new ModelException("Ingrese una nave válida.");
        
        // Verifica que la nave sea válida
        verificarNave(nave);
        
        Coordenada[] coordenadasNave = nave.obtenerCoordenadas();
        // Verifica si alguna de las casillas ya está ocupada (para cada coordenada que abarca la nave)
        for(Coordenada coordenada : coordenadasNave){
            if(casillas[coordenada.obtenerX()][coordenada.obtenerY()].estaOcupada())
                throw new ModelException("Ya se encuentra una nave en la posición.");
        }
        // Actualiza el estado de cada casilla como ocupada
        for(Coordenada coordenada : coordenadasNave)
            casillas[coordenada.obtenerX()][coordenada.obtenerY()].ocuparCasilla();
        
        // Se agrega la nave
        naves.add(nave);
        // Se regresa true
        return true;
    }

    @Override
    public boolean eliminarNave(Nave nave) throws ModelException {
        
        // Verifica que la coordenada exista y tenga coordenadas
        if(nave == null || nave.obtenerCoordenadas() == null || nave.getTipo() == null)
            throw new ModelException("Ingrese una nave válida.");
        
        // Verifica que la nave sea válida
        verificarNave(nave);
        
        // Obtiene las coordenadas de la nave a eliminar
        Coordenada[] coordenadasNave = nave.obtenerCoordenadas();
        
        // Recorre cada nave del conjunto de naves del tablero
        for(Nave naveActual: naves){
            // Guarda si la nave actual del conjunto de naves es igual a la nave recibida
            boolean igual = true;
            // Primero verifica que sean del mismo tipo de nave
            if(naveActual.getTipo() == nave.getTipo()){
                // Obtiene las coordenadas de la nave actual del conjunto de naves del tablero
                Coordenada[] coordenadasNaveActual = naveActual.obtenerCoordenadas();
                // Compara si las coordenadas de la nave actual son exactamente las mismas que las de la nave recibida
                for (int i = 0; i < coordenadasNaveActual.length; i++) {
                    if(!coordenadasNaveActual[i].compararCoordenada(coordenadasNave[i])){
                        igual = false;
                        break;
                    }
                }
                // Desocupa todas las casillas de la nave y elimina la nave del conjunto de naves
                if(igual){
                    for(Coordenada coordenada: coordenadasNaveActual)
                        casillas[coordenada.obtenerX()][coordenada.obtenerY()].desocuparCasilla();
                    naves.remove(naveActual);
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean colocarNaves(List<Nave> naves) throws ModelException{
        // Lista de naves agregadas
        List<Nave> navesAgregadas = new ArrayList<>();
        try {
            // Intenta agregar cada nave al tablero
            for(Nave nave: naves){
                // Agrega la nave al conjunto de naves del tablero
                añadirNave(nave);
                // Agrega la nave a la lista de naves agregadas
                navesAgregadas.add(nave);
            }
            
            return true;
            
        } catch (ModelException e) {
            // Si la lista de naves agregadas no está vacía
            if(!navesAgregadas.isEmpty())
                // Elimina cada nave agregada del tablero
                for(Nave naveAgregada : navesAgregadas)
                    eliminarNave(naveAgregada);
            
            throw new ModelException("No se pudieron agregar todas las naves al tablero. Verifique la posición de cada una.");
        }
    }
    
//    private boolean validarPosicionamientoNaves(List<Nave> naves) {
//        Set<String> coordenadasOcupadas = new HashSet<>();
//
//        for (Nave nave : naves) {
//            for (Coordenada coord : nave.obtenerCoordenadas()) {
//                // Validar que esté dentro del tablero
//                if (coord.obtenerX() < 0 || coord.obtenerX() >= tamanio ||
//                    coord.obtenerY() < 0 || coord.obtenerY() >= tamanio) {
//                    return false;
//                }
//
//                String key = coord.obtenerX() + "," + coord.obtenerY();
//                if (coordenadasOcupadas.contains(key)) {
//                    return false; // Se sobrep one con otra nave
//                }
//                coordenadasOcupadas.add(key);
//            }
//        }
//
//        return true;
//    }
    
    @Override
    public Nave encontrarNaveEnCoordenada(Coordenada coordenada){
        for (Nave nave : naves) 
            for (Coordenada coord : nave.obtenerCoordenadas()) 
                if (coord.equals(coordenada)) 
                    return nave;
        return null;
    }
    
    @Override
    public EstadoCasilla recibirImpacto(Coordenada coordenada) throws ModelException {
        // Verifica que la coordenada exista.
        if(coordenada == null)
            throw new ModelException("Ingrese una coordenada válida.");
        
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
                    // Incrementa contador de naves hundidas
                    navesHundidas++;
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
    private void inicializarCasillas() {
        for (int i = 0; i < getTamanio(); i++) 
            for (int j = 0; j < getTamanio(); j++) 
                casillas[i][j] = new Casilla(new Coordenada(i, j));
    }
    
    /**
     * 
     * @param coordenadas
     * @throws ModelException 
     */
    private void verificarNave(Nave nave) throws ModelException{
        
        Coordenada[] coordenadas = nave.obtenerCoordenadas();
        TipoNave tipo = nave.getTipo();
        
        // Verifica la cantidad de coordenadas, de acuerdo al tipo de nave
        switch(coordenadas.length){
            case 1 ->{
                if(tipo != TipoNave.BARCO)
                    throw new ModelException("Un barco debe abarcar 1 casilla.");
                break;
            }
            case 2 ->{
                if(tipo != TipoNave.SUBMARINO)
                    throw new ModelException("Un submarino debe abarcar 2 casillas.");
                break;
            }
            case 3 ->{
                if(tipo != TipoNave.CRUCERO)
                    throw new ModelException("Un crucero debe abarcar 3 casillas.");
                break;
            }
            default ->{
                if(tipo != TipoNave.PORTAAVIONES)
                    throw new ModelException("Un portaaviones debe abarcar 4 casillas.");
            }
        }
        
        // Verifica que la nave esté dentro del tablero
        for(Coordenada coordenada: coordenadas){
            if (coordenada.obtenerX() < 0 || coordenada.obtenerX() >= tamanio ||
                coordenada.obtenerY() < 0 || coordenada.obtenerY() >= tamanio) {
                throw new ModelException("Las coordenadas de la nave se encuentran fuera del tablero.");
            }
        }
        
        
        // Verifica las coordenadas, de acuerdo a la orientación (queda pendiente).
//        for (Coordenada coordenada : coordenadas) {
//            if(orientacion == OrientacionNave.HORIZONTAL){
//                
//            } else{
//                
//            }
//        }
    }
    
    @Override
    public boolean navesColocadas(){return naves.size() == totalNaves;}

    @Override
    public int getNavesHundidas() {return navesHundidas;}

    @Override
    public List<Nave> getNaves() {
        if(!naves.isEmpty()){
            List<Nave> navesLista = new ArrayList<>();
            naves.forEach(nave -> {
                navesLista.add(nave);
            });
            return navesLista;
        } else
            return null;
    }

    @Override
    public Casilla[][] getCasillas() {return casillas;}

    @Override
    public int getTotalNaves() {return totalNaves;}

    @Override
    public boolean todasNavesHundidas() {return totalNaves > 0 && navesHundidas >= totalNaves;}
}