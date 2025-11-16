package views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import static views.ConstantesVista.COLOR_BORDE;
import static views.ConstantesVista.COLOR_FONDO;

/**
 * Contenedor de las naves a ser arrastradas al tablero de naves.
 * Clase exclusiva del caso de uso 2: configurar naves.
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaArrastreNaves extends JPanel{
    
    /**
     * Creates new form VistaMarcador
     */
    public VistaArrastreNaves() {
        incializarComponentes();
    }
    
    private void incializarComponentes(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setPreferredSize(new Dimension(250, 0));
        
        JLabel lblTitulo = new JLabel("NAVES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(COLOR_BORDE);
        add(lblTitulo, 0);
        add(Box.createVerticalStrut(15), 1);
        
        cargarBarcos();
    }
    
    private void cargarBarcos(){
//        Icon portaaviones = new ImageIcon("src/main/java/imgs/casillas.png");
//        JLabel portaavionesLabel1 = new JLabel(portaaviones, JLabel.CENTER);
//        DragMouseAdapter listener = new DragMouseAdapter();
//        portaavionesLabel1.addMouseListener(listener);
//        portaavionesLabel1.setTransferHandler(new TransferHandler("portaaviones"));
        NaveArrastrable imagenNave = new NaveArrastrable(2);
        NaveArrastrable imagenNave2 = new NaveArrastrable(4);
        try {
            Image portaaviones = ImageIO.read(getClass().getResource("/imgs/casillas.png"));
            ImageIcon portaavionesIcon = new ImageIcon(portaaviones);
            imagenNave.setIcon(portaavionesIcon);
            imagenNave.setOverbearing(true);
            imagenNave.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            Image portaaviones2 = ImageIO.read(getClass().getResource("/imgs/casillas3.png"));
            ImageIcon portaavionesIcon2 = new ImageIcon(portaaviones2);
            imagenNave2.setIcon(portaavionesIcon2);
            imagenNave2.setOverbearing(true);
            imagenNave2.setAlignmentX(Component.CENTER_ALIGNMENT);
            
//            ImageIcon icono = new ImageIcon(portaaviones2);
//            JLabel label = new JLabel(icono);
            add(imagenNave, 2);
            add(Box.createVerticalStrut(15), 3);
            add(imagenNave2, 4);
//            add(label);
        } catch (IOException ex) {
            System.getLogger(VistaArrastreNaves.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
//    private class DragMouseAdapter extends MouseAdapter {
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//
//            var c = (JComponent) e.getSource();
//            var handler = c.getTransferHandler();
//            handler.exportAsDrag(c, e, TransferHandler.MOVE);
//        }
//    }
}