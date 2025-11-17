package mx.itson.utils.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.itson.utils.enums.EstadoCasilla;

/**
 * TableroDTO - Data Transfer Object para el estado del tablero
 *
 * Se usa para transferir el estado completo del tablero de un jugador
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class TableroDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idJugador;
    private String nombreJugador;
    private EstadoCasilla[][] casillas; // Matriz del tablero
    private List<NaveDTO> naves;
    private int navesHundidas;
    private int totalNaves;

    /**
     * Constructor vacío
     */
    public TableroDTO() {
        this.naves = new ArrayList<>();
    }

    /**
     * Constructor con parámetros
     *
     * @param idJugador
     * @param nombreJugador
     * @param tamano Tamaño del tablero (ej: 10 para 10x10)
     */
    public TableroDTO(String idJugador, String nombreJugador, int tamano) {
        this.idJugador = idJugador;
        this.nombreJugador = nombreJugador;
        this.casillas = new EstadoCasilla[tamano][tamano];
        this.naves = new ArrayList<>();
        this.navesHundidas = 0;
        this.totalNaves = 0;

        // Inicializar todas las casillas como vacías
        for (int i = 0; i < tamano; i++) {
            for (int j = 0; j < tamano; j++) {
                casillas[i][j] = EstadoCasilla.VACIA;
            }
        }
    }

    // Getters y Setters
    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public EstadoCasilla[][] getCasillas() {
        return casillas;
    }

    public void setCasillas(EstadoCasilla[][] casillas) {
        this.casillas = casillas;
    }

    public List<NaveDTO> getNaves() {
        return naves;
    }

    public void setNaves(List<NaveDTO> naves) {
        this.naves = naves;
        this.totalNaves = naves.size();
    }

    public int getNavesHundidas() {
        return navesHundidas;
    }

    public void setNavesHundidas(int navesHundidas) {
        this.navesHundidas = navesHundidas;
    }

    public int getTotalNaves() {
        return totalNaves;
    }

    public void setTotalNaves(int totalNaves) {
        this.totalNaves = totalNaves;
    }

    /**
     * Verifica si todas las naves están hundidas
     *
     * @return true si todas las naves están hundidas
     */
    public boolean todasNavesHundidas() {
        return navesHundidas >= totalNaves && totalNaves > 0;
    }

    /**
     * Crea una copia profunda del tablero
     * @return Nueva instancia de TableroDTO con los mismos datos
     */
    public TableroDTO copiarProfundo() {
        TableroDTO copia = new TableroDTO();
        copia.idJugador = this.idJugador;
        copia.nombreJugador = this.nombreJugador;
        copia.navesHundidas = this.navesHundidas;
        copia.totalNaves = this.totalNaves;

        // Copiar naves
        if (this.naves != null) {
            copia.naves = new ArrayList<>(this.naves);
        }

        // Copiar matriz de casillas (copia profunda)
        if (this.casillas != null) {
            int filas = this.casillas.length;
            int columnas = this.casillas[0].length;
            copia.casillas = new EstadoCasilla[filas][columnas];

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    copia.casillas[i][j] = this.casillas[i][j];
                }
            }
        }

        return copia;
    }

    @Override
    public String toString() {
        return "TableroDTO{"
                + "jugador='" + nombreJugador + '\''
                + ", naves=" + navesHundidas + "/" + totalNaves + " hundidas" + '\''
                + '}';
    }
}
  