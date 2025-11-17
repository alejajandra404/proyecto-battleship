package views;

import controllers.ControladorConfigurarJugador;
import controllers.ControladorConfigurarJugador.IVistaConfigurarJugador;
import mx.itson.utils.dtos.JugadorDTO;
import javax.swing.*;
import java.awt.*;

/**
 * Vista para configurar el jugador (nombre y color)
 * Implementa la interfaz IVistaConfigurarJugador del controlador
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaConfigurarJugador extends JPanel implements IVistaConfigurarJugador {

    private final ControladorConfigurarJugador controlador;

    // Componentes de la UI
    private JLabel lblTitulo;
    private JLabel lblNombre;
    private JLabel lblColor;
    private JLabel lblEstado;
    private JTextField txtNombre;
    private JComboBox<String> cmbColor;
    private JButton btnConectar;
    private JPanel panelFormulario;
    private JPanel panelEstado;

    /**
     * Constructor
     */
    public VistaConfigurarJugador() {
        this.controlador = new ControladorConfigurarJugador();
        this.controlador.setVista(this);
        initComponents();
    }

    /**
     * Inicializa los componentes de la vista
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1216, 680));
        setBackground(new Color(240, 248, 255));

        // Panel principal con el formulario
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridBagLayout());
        panelFormulario.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        lblTitulo = new JLabel("CONFIGURACIÓN DEL JUGADOR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(lblTitulo, gbc);

        // Espacio
        gbc.gridy = 1;
        panelFormulario.add(Box.createVerticalStrut(30), gbc);

        // Label Nombre
        lblNombre = new JLabel("Nombre del jugador:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(lblNombre, gbc);

        // Campo de texto Nombre
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        txtNombre.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txtNombre, gbc);

        // Label Color
        lblColor = new JLabel("Color:");
        lblColor.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(lblColor, gbc);

        // ComboBox Color
        cmbColor = new JComboBox<>(new String[]{"Rojo", "Azul"});
        cmbColor.setFont(new Font("Arial", Font.PLAIN, 16));
        cmbColor.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(cmbColor, gbc);

        // Espacio
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panelFormulario.add(Box.createVerticalStrut(20), gbc);

        // Botón Conectar
        btnConectar = new JButton("Conectar y Registrar");
        btnConectar.setFont(new Font("Arial", Font.BOLD, 16));
        btnConectar.setPreferredSize(new Dimension(250, 45));
        btnConectar.setBackground(new Color(70, 130, 180));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFocusPainted(false);
        btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConectar.addActionListener(e -> manejarConexion());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(btnConectar, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // Panel de estado en la parte inferior
        panelEstado = new JPanel();
        panelEstado.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelEstado.setBackground(new Color(220, 235, 255));
        panelEstado.setPreferredSize(new Dimension(1216, 60));

        lblEstado = new JLabel("Esperando configuración...");
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 14));
        lblEstado.setForeground(new Color(100, 100, 100));
        panelEstado.add(lblEstado);

        add(panelEstado, BorderLayout.SOUTH);
    }

    /**
     * Maneja el evento del botón conectar
     */
    private void manejarConexion() {
        String nombre = txtNombre.getText().trim();
        String colorSeleccionado = (String) cmbColor.getSelectedItem();

        // Convertir string a Color
        Color color = colorSeleccionado.equals("Rojo") ? Color.RED : Color.BLUE;

        // Delegar al controlador
        controlador.iniciarConexionYRegistro(nombre, color);
    }

    // Implementación de IVistaConfigurarJugador

    @Override
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Información",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    @Override
    public void mostrarError(String error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                error,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            lblEstado.setText("Error: " + error);
            lblEstado.setForeground(Color.RED);
        });
    }

    @Override
    public void habilitarFormulario(boolean habilitar) {
        SwingUtilities.invokeLater(() -> {
            txtNombre.setEnabled(habilitar);
            cmbColor.setEnabled(habilitar);
            btnConectar.setEnabled(habilitar);
        });
    }

    @Override
    public void mostrarProgreso(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.setForeground(new Color(70, 130, 180));
        });
    }

    @Override
    public void registroExitoso(JugadorDTO jugador) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("¡Registro exitoso! Bienvenido, " + jugador.getNombre());
            lblEstado.setForeground(new Color(0, 150, 0));

            // Mostrar mensaje de confirmación
            JOptionPane.showMessageDialog(
                this,
                "Registro exitoso!\n" +
                "Nombre: " + jugador.getNombre() + "\n" +
                "ID: " + jugador.getId().substring(0, 8) + "...",
                "¡Bienvenido!",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Avanzar a la vista de lista de jugadores
            FlujoVista.mostrarListaJugadores(controlador.getServicioConexion(), jugador);
        });
    }

    @Override
    public void nombreDuplicado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("Nombre duplicado");
            lblEstado.setForeground(Color.ORANGE);

            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Nombre en uso",
                JOptionPane.WARNING_MESSAGE
            );

            // Limpiar el campo de nombre para que el usuario ingrese otro
            txtNombre.setText("");
            txtNombre.requestFocus();
        });
    }

    /**
     * Limpia recursos al cerrar la vista
     */
    public void limpiar() {
        if (controlador != null) {
            controlador.limpiar();
        }
    }

    /**
     * Obtiene el controlador asociado
     * @return ControladorConfigurarJugador
     */
    public ControladorConfigurarJugador getControlador() {
        return controlador;
    }
}
