package mx.itson.models;

import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoNave;
import mx.itson.utils.enums.OrientacionNave;
import mx.itson.utils.enums.TipoNave;

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
public class Nave {
    
    private final TipoNave tipo;
    private EstadoNave estado;
    private final OrientacionNave orientacion;
    private final Coordenada[] coordenadas;
    private int impactosRecibidos;
    
    /**
     * Constructor para crear una nueva Nave
     * @param tipo El tipo de nave (ej. PORTAAVIONES, CRUCERO)
     * @param orientacion La orientación de la nave (VERTICAL u HORIZONTAL)
     * @param coordenadas
     */
    public Nave(TipoNave tipo, OrientacionNave orientacion, Coordenada[] coordenadas) throws ModelException {
        this.tipo = tipo;
        this.orientacion = orientacion;
        verificarCoordenadas(coordenadas);
        this.coordenadas = coordenadas;
        this.estado = EstadoNave.INTACTA; //Todas las naves empiezan intactas
        this.impactosRecibidos = 0;
    }
    
    // --- Setters --- //
    
    /**
     * Establece el estado de la nave.
     * @param estado Nuevo estado de la nave.
     */
    public void setEstado(EstadoNave estado) {this.estado = estado;}
    
    /**
     * Establece la cantidad de impactos recibidos de la nave.
     * @param impactosRecibidos Nueva cantidad de impactos recibidos de la nave.
     */
    public void setImpactosRecibidos(int impactosRecibidos) {this.impactosRecibidos = impactosRecibidos;}
    
    // --- Getters --- //
    
    /**
     * Regresa el estado de la nave.
     * @return Estado de la nave.
     */
    public EstadoNave getEstado() {return estado;}
    
    /**
     * Devuelve la cantidad de impactos recibidos de la nave.
     * @return Cantidad de impactos recibidos de la nave.
     */
    public int getImpactosRecibidos() {return impactosRecibidos;}
    
    /**
     * Obtiene la longitud de la nave, que es equivalente al número de coordenadas que ocupa
     * @return El tamaño de la nave
     */
    public int getLongitud() {return this.coordenadas.length;}
    
    /**
     * 
     * @return 
     */
    public TipoNave getTipo() {return tipo;}
    
    /**
     * 
     * @return 
     */
    public OrientacionNave obtenerOrientacion() {return orientacion;}
    
    /**
     * 
     * @return 
     */
    public EstadoNave verificarEstado() {return estado;}
    
    /**
     * 
     * @return 
     */
    public Coordenada[] obtenerCoordenadas() {return coordenadas;}
    
    /**
     * Procesa un disparo en una coordenada específica. La lógica para encontrar
     * y registrar el impacto está consolidada dentro de este método
     * @param coordenadaImpacto
     * @return 
     */
    public boolean recibirImpacto(Coordenada coordenadaImpacto) {
        // Recorre las coordenadas de la nave
        for(Coordenada coordenada: coordenadas){
            // Si alguna coordenada coincide con la recibida, se actualiza el estado del barco
            if(coordenada.equals(coordenadaImpacto)){
                impactosRecibidos++;
                actualizarEstado();
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica y actualiza el estado de la nave (INTACTA, AVERIADA, HUNDIDA)
     * basado en el número de impactos recibidos
     * @return El nuevo estado de la nave
     */
    private void actualizarEstado() {
        if (impactosRecibidos >= getLongitud()) 
            this.estado = EstadoNave.HUNDIDA;
         else 
            this.estado = EstadoNave.AVERIADA;
    }

    /**
     * Comprueba si la nave ha sido hundida
     * @return true si la nave está hundida, false en caso contrario
     */
    public boolean estaHundida() {return this.impactosRecibidos >= getLongitud();}
    
    /**
     * 
     * @param coordenadas
     * @throws ModelException 
     */
    private void verificarCoordenadas(Coordenada[] coordenadas) throws ModelException{
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
        // Verifica las coordenadas, de acuerdo a la orientación (queda pendiente).
//        for (Coordenada coordenada : coordenadas) {
//            if(orientacion == OrientacionNave.HORIZONTAL){
//                
//            } else{
//                
//            }
//        }
    }
}