package mx.itson.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.OrientacionNave;
import mx.itson.utils.enums.TipoNave;

/**
 * TableroNavesServidor.java
 *
 * Clase que representa el tablero de naves de un jugador en el servidor.
 * Contiene las naves del jugador y gestiona los impactos recibidos del oponente.
 * Valida la colocación de naves y mantiene el registro de naves hundidas.
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
    private final int totalPortaaviones = 2;
    private final int totalCruceros = 2;
    private final int totalSubmarinos = 4;
    private final int totalBarcos = 3;

    /**
     * Constructor de la clase TableroNavesServidor.
     * Inicializa el tablero con el tamaño especificado, crea la matriz de casillas
     * y el conjunto de naves.
     *
     * @param tamanio Tamaño del tablero
     * @throws ModelException Si el tamaño es inválido
     */
    public TableroNavesServidor(int tamanio) throws ModelException {
        super(tamanio);
        this.casillas = new Casilla[tamanio][tamanio];
        this.naves = new HashSet<>();
        inicializarCasillas();
    }

    /**
     * Añade una nave al tablero.
     * Valida que la nave sea válida y que las casillas no estén ocupadas.
     *
     * @param nave Nave a añadir
     * @return true si se añadió exitosamente
     * @throws ModelException Si la nave es inválida o la posición está ocupada
     */
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

    /**
     * Elimina una nave del tablero.
     * Busca la nave en el conjunto y desocupa sus casillas.
     *
     * @param nave Nave a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     * @throws ModelException Si la nave es inválida
     */
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

    /**
     * Coloca todas las naves en el tablero.
     * Valida que se coloquen todas las naves requeridas por tipo.
     *
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente todas las naves
     * @throws ModelException Si faltan naves o hay errores en la colocación
     */
    @Override
    public boolean colocarNaves(List<Nave> naves) throws ModelException{
        // Lista de naves agregadas
        List<Nave> navesAgregadas = new ArrayList<>();
        // Variables para guardar la cantidad de cada tipo de naves del conjunto de naves recibido
        int portaaviones = 0, cruceros = 0, submarinos = 0, barcos = 0;
        // Recorre cada nave
        for(Nave nave: naves){
            // Obtiene el tipo de cada nave
            switch(nave.getTipo()){
                // Aumenta la cantidad de tipo de nave del conjunto
                case TipoNave.PORTAAVIONES -> {portaaviones++; break;} 
                case TipoNave.CRUCERO ->{cruceros++; break;} 
                case TipoNave.SUBMARINO ->{submarinos++; break;} 
                case TipoNave.BARCO ->{barcos++;}
            }
        }
        /*
            Valores booleanos para almacenar true si la cantidad de cada tipo corresponde
            con la cantidad esperada por el tablero.
        */
        boolean todosPortaaviones = portaaviones == totalPortaaviones;
        boolean todosCruceros = cruceros == totalCruceros;
        boolean todosSubmarinos = submarinos == totalSubmarinos;
        boolean todosBarcos = barcos == totalBarcos;
        // Si alguno de los valores es falso (faltan tipos de naves)
        if(!todosPortaaviones || !todosCruceros || !todosSubmarinos || !todosBarcos){
            // Mensaje de error para ser lanzado por la excepción
            String mensajeError = """
                                  Agregue todas las naves para poder iniciar la partida.
                                  Faltan las siguientes naves:
                                  """;
            // Si faltan portaaviones, se explica la cantidad faltante
            if(!todosPortaaviones)
                mensajeError += String.format("\n- Portaaviones: %d", (totalPortaaviones - portaaviones));
            // Si faltan cruceros, se explica la cantidad faltante
            if(!todosCruceros)
                mensajeError += String.format("\n- Cruceros: %d", (totalCruceros - cruceros));
            // Si faltan submarinos, se explica la cantidad faltante
            if(!todosSubmarinos)
                mensajeError += String.format("\n- Submarinos: %d", (totalSubmarinos - submarinos));
            // Si faltan barcos, se explica la cantidad faltante
            if(!todosBarcos)
                mensajeError += String.format("\n- Barcos: %d", (totalBarcos - barcos));
            // Se lanza la excepción con el mensaje de error personalizado
            throw new ModelException(mensajeError);
        }
        // Si se recibió el total de naves esperado
        try {
            // Intenta agregar cada nave al tablero
            for(Nave nave: naves){
                // Agrega la nave al conjunto de naves del tablero
                añadirNave(nave);
                // Agrega la nave a la lista de naves agregadas
                navesAgregadas.add(nave);
            }
            // Confirma la exitosa colocación de las naves
            return true;
            
        } catch (ModelException e) {
            // Si la lista de naves agregadas no está vacía
            if(!navesAgregadas.isEmpty())
                // Elimina cada nave agregada del tablero
                for(Nave naveAgregada : navesAgregadas)
                    eliminarNave(naveAgregada);
            // Lanza la excepción correspondiente
            throw new ModelException("No se pudieron agregar todas las naves al tablero. Verifique la posición de cada una.");
        }
    }

    /**
     * Busca y retorna la nave ubicada en una coordenada específica.
     *
     * @param coordenada Coordenada a buscar
     * @return La nave en esa coordenada, o null si no hay ninguna
     */
    @Override
    public Nave encontrarNaveEnCoordenada(Coordenada coordenada){
        for (Nave nave : naves) 
            for (Coordenada coord : nave.obtenerCoordenadas()) 
                if (coord.equals(coordenada)) 
                    return nave;
        return null;
    }

    /**
     * Recibe un impacto en una coordenada específica.
     * Actualiza el estado de la casilla y la nave si es impactada.
     *
     * @param coordenada Coordenada del impacto
     * @return Estado de la casilla después del impacto
     * @throws ModelException Si la coordenada es inválida o ya fue impactada
     */
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
     * Inicializa la matriz de casillas vacías.
     * Crea una casilla para cada posición del tablero.
     */
    private void inicializarCasillas() {
        for (int i = 0; i < getTamanio(); i++) 
            for (int j = 0; j < getTamanio(); j++) 
                casillas[i][j] = new Casilla(new Coordenada(i, j));
    }

    /**
     * Verifica que una nave sea válida.
     * Valida el tipo, tamaño, orientación y que esté dentro del tablero.
     *
     * @param nave Nave a verificar
     * @throws ModelException Si la nave no cumple con las validaciones
     */
    private void verificarNave(Nave nave) throws ModelException{
        // Obtiene las coordenadas de la nave
        Coordenada[] coordenadas = nave.obtenerCoordenadas();
        // Obtiene el tipo de nave
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
        
        // Verifica las coordenadas de acuerdo a la orientación de la nave.
        if(nave.obtenerOrientacion() == OrientacionNave.HORIZONTAL){
            // Almacena el valor de x de la primera coordenada (todas las coordenadas deberían tener el mismo valor de x)
            int mismaX = coordenadas[0].obtenerX();
            // Conjunto de valores de y de las coordenadas (valida que los valores de y sean distintos entre sí)
            Set<Integer> y = new HashSet<>();
            // Recorre cada coordenada
            for (Coordenada c : coordenadas) {
                // Verifica que cada coordenada tenga el mismo valor de x
                if (c.obtenerX() != mismaX) 
                    throw new ModelException("Las coordenas de la nave no corresponden con su orientación.");
                // Agrega el valor de y al conjunto de valores de y de las coordenadas
                y.add(c.obtenerY());
            }
            // Obtiene el mínimo y máximo del conjunto de valores de y de las coordenadas
            int minY = Collections.min(y);
            int maxY = Collections.max(y);
            /*
                Verifica que la resta del valor de y máximo menos el valor de y mínimo 
                del conjunto de coordenadas sea igual al tamaño del arreglo de coordenadas.
                Esto valida que las coordenadas correspondan con la cantidad de casillas que
                abarca la nave, y que estas casillas sean subsecuentes entre sí (que no haya
                huecos entre casillas).
            */
            if (maxY - minY + 1 != coordenadas.length)
                throw new ModelException("Las coordenas de la nave no corresponden con su orientación.");
        } else{
            // Almacena el valor de y de la primera coordenada (todas las coordenadas deberían tener el mismo valor de y)
            int mismaY = coordenadas[0].obtenerY();
            // Conjunto de valores de x de las coordenadas (valida que los valores de x sean distintos entre sí)
            Set<Integer> x = new HashSet<>();
            // Recorre cada coordenada
            for (Coordenada c : coordenadas) {
                // Verifica que cada coordenada tenga el mismo valor de y
                if (c.obtenerY() != mismaY) 
                    throw new ModelException("Las coordenas de la nave no corresponden con su orientación.");
                // Agrega el valor de x al conjunto de valores de x de las coordenadas
                x.add(c.obtenerX());
            }
            // Obtiene el mínimo y máximo del conjunto de valores de x de las coordenadas
            int minX = Collections.min(x);
            int maxX = Collections.max(x);
            /*
                Verifica que la resta del valor de x máximo menos el valor de x mínimo 
                del conjunto de coordenadas sea igual al tamaño del arreglo de coordenadas.
                Esto valida que las coordenadas correspondan con la cantidad de casillas que
                abarca la nave, y que estas casillas sean subsecuentes entre sí (que no haya
                huecos entre casillas).
            */
            if (maxX - minX + 1 != coordenadas.length) 
                throw new ModelException("Las coordenas de la nave no corresponden con su orientación.");
        }

    }

    /**
     * Verifica si todas las naves han sido colocadas en el tablero.
     *
     * @return true si todas las naves están colocadas
     */
    @Override
    public boolean navesColocadas(){return naves.size() == (totalPortaaviones + totalCruceros + totalSubmarinos + totalBarcos);}

    /**
     * Obtiene el número de naves hundidas.
     *
     * @return Cantidad de naves hundidas
     */
    @Override
    public int getNavesHundidas() {return navesHundidas;}

    /**
     * Obtiene la lista de todas las naves en el tablero.
     *
     * @return Lista de naves, o null si no hay naves
     */
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

    /**
     * Obtiene la matriz de casillas del tablero.
     *
     * @return Matriz bidimensional de casillas
     */
    @Override
    public Casilla[][] getCasillas() {return casillas;}

    /**
     * Obtiene el número total de naves que debe tener el tablero.
     *
     * @return Total de naves requeridas
     */
    @Override
    public int getTotalNaves() {return totalPortaaviones + totalCruceros + totalSubmarinos + totalBarcos;}

    /**
     * Verifica si todas las naves del tablero han sido hundidas.
     *
     * @return true si todas las naves están hundidas
     */
    @Override
    public boolean todasNavesHundidas() {
        int totalNaves = totalPortaaviones + totalCruceros + totalSubmarinos + totalBarcos;
        return totalNaves > 0 && navesHundidas >= totalNaves;
    }
}