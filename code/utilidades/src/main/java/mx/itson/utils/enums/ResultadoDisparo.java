package mx.itson.utils.enums;
/**
 * ResultadoDisparo.java
 *
 * Enumeración que representa los posibles resultados 
 * de un disparo en la partida.
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
 *
 */
public enum ResultadoDisparo {
    /** Disparo impactó una nave y la averió */
    IMPACTO_AVERIADA,

    /** Disparo impactó y hundió completamente una nave */
    IMPACTO_HUNDIDA,

    /** Disparo cayó al agua, no hay nave */
    AGUA
}