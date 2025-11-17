package mx.itson.utils.dtos;

import java.io.Serializable;
import mx.itson.utils.enums.TipoMensaje;

/**
 * DTO genérico para mensajes entre cliente y servidor
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class MensajeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoMensaje tipo;
    private String contenido;
    private Object datos;

    /**
     * Constructor por defecto
     */
    public MensajeDTO() {
    }

    /**
     * Constructor con tipo y contenido
     * @param tipo Tipo de mensaje
     * @param contenido Contenido del mensaje
     */
    public MensajeDTO(TipoMensaje tipo, String contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    /**
     * Constructor completo
     * @param tipo Tipo de mensaje
     * @param contenido Contenido del mensaje
     * @param datos Datos adicionales
     */
    public MensajeDTO(TipoMensaje tipo, String contenido, Object datos) {
        this.tipo = tipo;
        this.contenido = contenido;
        this.datos = datos;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }

    @Override
    public String toString() {
        return "MensajeDTO{" +
                "tipo=" + tipo +
                ", contenido='" + contenido + '\'' +
                '}';
    }
    
}
