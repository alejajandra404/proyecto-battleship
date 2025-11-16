package views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Esta clase toma prestado los métodos especificados en la clase DraggableComponent,
 * Desarrollada por De Gregorio Daniele, y le agregué código personalizado extra.
 * (No pude heredar su clase, ya que recurrí a utilizar JLabel para que se ajustara
 * correctamente la imagen, caso contrario al utilizar la clase DraggableImageComponent,
 * también desarrollada por el mismo autor).
 * @author PC WHITE WOLF
 */
public class NaveArrastrable extends JLabel {
    /** If sets <b>TRUE</b> this component is draggable */
    private boolean draggable = true;
    /** 2D Point representing the coordinate where mouse is, relative parent container */
    private Point anchorPoint;
    /** Default mouse cursor for dragging action */
    private Cursor draggingCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    /** If sets <b>TRUE</b> when dragging component, it will be painted over each other (z-Buffer change) */
    private boolean overbearing = false;
    
    /*
        *----------------------------------Inicio de código personalizado------------------------------------------------*
    */
    
    // Contenedor original padre y contenedor raiz
    protected JComponent contenedorPadreOriginal;
    protected JRootPane contenedorRaiz; // Para la ventana principal
    
    // Coordenadas del componente
    protected int positionX;
    protected int positionY;
    
    /*
        *----------------------------------Fin de código personalizado------------------------------------------------*
    */
    
    private int orden;
    
    public NaveArrastrable(int orden) {
        this.orden = orden;
        addDragListeners();
        setOpaque(true);
        setBackground(new Color(240,240,240));
    }

    public int getOrden() {return orden;}

    public void setOrden(int orden) {this.orden = orden;}
    
    /**
     * We have to define this method because a JComponent is a void box. So we have to
     * define how it will be painted. We create a simple filled rectangle.
     *
     * @param g Graphics object as canvas
     */
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (isOpaque()) {
//            g.setColor(getBackground());
//            g.fillRect(0, 0, getWidth(), getHeight());
//        }
//    }
    
    /**
     * Add Mouse Motion Listener with drag function
     */
    private void addDragListeners() {
        /** This handle is a reference to THIS beacause in next Mouse Adapter "this" is not allowed */
        final NaveArrastrable handle = this;
        
        /*
            *----------------------------------Inicio de código personalizado------------------------------------------------*
        */
        
        // Mouse listener para el cambio de contenedor del componente
        addMouseListener(new MouseAdapter() {
            // Se mantiene seleccionado al componente (mouse presionado)
            @Override
            public void mousePressed(MouseEvent e) {
                // Verifica que la opción de arrastrable esté activada (true) y el componente tenga un contendor
                if (!draggable || getParent() == null) 
                    return;
                
                // 1.- Guarda el contenedor padre
                contenedorPadreOriginal = (JComponent) getParent();

                // 2.- Obtiene el contenedor raíz
                contenedorRaiz = handle.getRootPane();

                // 3.- Obtiene el punto relativo al contenedor padre
                Point ubicacionActual = SwingUtilities.convertPoint(contenedorPadreOriginal, handle.getLocation(), contenedorRaiz);

                // 4.- Quita el componente de su padre original y lo repinta
                contenedorPadreOriginal.remove(handle);
                contenedorPadreOriginal.repaint();

                // 5.- Agrega el componente al contenedor raíz
                 if (contenedorRaiz != null) 
                     contenedorRaiz.getLayeredPane().add(handle, JLayeredPane.DRAG_LAYER);
                
                // 5. Establece la nueva ubicación y hace que el nuevo padre lo repinte
                handle.setLocation(ubicacionActual);
                handle.repaint();

                // Si la opción de overbearing está activada (superposición), se asegura que el componente esté en la capa superior.
                if (overbearing && contenedorRaiz != null) 
                     contenedorRaiz.getLayeredPane().setComponentZOrder(handle, 0);
            }
            
            // Se deselecciona el componente (mouse suelto)
            @Override
            public void mouseReleased(MouseEvent e) {
                // Verifica que la opción de arrastrable esté activada (true) y el componente tenga un contendor
                if (!draggable || contenedorPadreOriginal == null) 
                    return;
                // 1.- Se obtiene el contenedor actual, y se castea como LayeredPane
                JLayeredPane currentParent = (JLayeredPane) getParent();

                // 2.- Se obtiene la ubicación relativa al contenedor original
                Point currentLoc = SwingUtilities.convertPoint(currentParent, handle.getLocation(), contenedorPadreOriginal);

                // 3.- Se quita el componente del contenedor actual y se repinta
                currentParent.remove(handle);
                currentParent.repaint();

                // 4.- Se agrega el componente a su contenedor original
                contenedorPadreOriginal.add(handle, orden);
                contenedorPadreOriginal.revalidate();

                // 5.- Se posiciona el componente en su ubicación original y se repinta
                handle.setLocation(currentLoc);
                handle.revalidate();
                handle.repaint();

                // 6.- Se resetea el contenedor padre original
                contenedorPadreOriginal = null;
            }
        });
        
        /*
            *----------------------------------Fin de código personalizado------------------------------------------------*
        */
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                anchorPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int anchorX = anchorPoint.x;
                int anchorY = anchorPoint.y;
                
                Point parentOnScreen = getParent().getLocationOnScreen();
                Point mouseOnScreen = e.getLocationOnScreen();
                
                /*
                    *----------------------------------Inicio de código personalizado------------------------------------------------*
                */
                
                positionX = mouseOnScreen.x - parentOnScreen.x - anchorX;
                positionY = mouseOnScreen.y - parentOnScreen.y - anchorY;
                
                /*
                    *----------------------------------Fin de código personalizado------------------------------------------------*
                */
                
                Point position = new Point(positionX, positionY);
                setLocation(position);

                //Change Z-Buffer if it is "overbearing"
                if (overbearing) {
                    getParent().setComponentZOrder(handle, 0);
                    repaint();
                }
            }
        });
    }


    /**
     * Remove all Mouse Motion Listener. Freeze component.
     */
    private void removeDragListeners() {
        for (MouseMotionListener listener : this.getMouseMotionListeners()) {
            removeMouseMotionListener(listener);
        }
        setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Get the value of draggable
     *
     * @return the value of draggable
     */
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Set the value of draggable
     *
     * @param draggable new value of draggable
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (draggable) {
            addDragListeners();
        } else {
            removeDragListeners();
        }

    }

    /**
     * Get the value of draggingCursor
     *
     * @return the value of draggingCursor
     */
    public Cursor getDraggingCursor() {
        return draggingCursor;
    }

    /**
     * Set the value of draggingCursor
     *
     * @param draggingCursor new value of draggingCursor
     */
    public void setDraggingCursor(Cursor draggingCursor) {
        this.draggingCursor = draggingCursor;
    }

    /**
     * Get the value of overbearing
     *
     * @return the value of overbearing
     */
    public boolean isOverbearing() {
        return overbearing;
    }

    /**
     * Set the value of overbearing
     *
     * @param overbearing new value of overbearing
     */
    public void setOverbearing(boolean overbearing) {
        this.overbearing = overbearing;
    }
}