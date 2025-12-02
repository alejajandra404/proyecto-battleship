package mx.itson.utils.dtos;

import java.io.Serializable;

/**
 *
 * @author Jesús Ernesto López Ibarra - 252663
 */
public class EstadisticaDTO implements Serializable {
    
    private String nombreJugador;
    private boolean esGanador;
    private int totalDisparos;
    private int aciertos;
    private int barcosHundidos;
    private double porcentajePrecision;
    
    public EstadisticaDTO(String nombreJugador, boolean esGanador, int totalDisparos, int aciertos ,int barcosHundidos) {
        this.nombreJugador = nombreJugador;
        this.esGanador = esGanador;
        this.aciertos = aciertos;
        this.totalDisparos = totalDisparos;
        this.barcosHundidos = barcosHundidos;
        
        if (totalDisparos > 0) {
            this.porcentajePrecision = (double) aciertos / totalDisparos * 100.0;
        } else {
            this.porcentajePrecision = 0.0;
        }
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public boolean isEsGanador() {
        return esGanador;
    }

    public void setEsGanador(boolean esGanador) {
        this.esGanador = esGanador;
    }

    public int getTotalDisparos() {
        return totalDisparos;
    }

    public void setTotalDisparos(int totalDisparos) {
        this.totalDisparos = totalDisparos;
    }

    public int getAciertos() {
        return aciertos;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getBarcosHundidos() {
        return barcosHundidos;
    }

    public void setBarcosHundidos(int barcosHundidos) {
        this.barcosHundidos = barcosHundidos;
    }

    public double getPorcentajePrecision() {
        return porcentajePrecision;
    }

    public void setPorcentajePrecision(double porcentajePrecision) {
        this.porcentajePrecision = porcentajePrecision;
    }
}