package views;

import controllers.ControladorListaJugadores;
import controllers.ControladorListaJugadores.IVistaListaJugadores;
import mx.itson.utils.dtos.JugadorDTO;
import mx.itson.utils.dtos.SolicitudPartidaDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista que muestra la lista de jugadores disponibles para jugar
 * Permite seleccionar un oponente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaListaJugadores extends JPanel implements IVistaListaJugadores {

    private final ControladorListaJugadores controlador;

    // Componentes de la UI
    private JLabel lblTitulo;
    private JLabel lblBienvenida;
    private JLabel lblEstado;
    private JTable tablaJugadores;
    private DefaultTableModel modeloTabla;
    private JButton btnSeleccionar;
    private JButton btnRefrescar;
    private JPanel panelSuperior;
    private JPanel panelCentro;
    private JPanel panelInferior;
    private JScrollPane scrollPane;

    /**
     * Constructor
     * @param controlador Controlador de lista de jugadores
     */
    public VistaListaJugadores(ControladorListaJugadores controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        initComponents();
        this.controlador.iniciar();
    }

    /**
     * Inicializa los componentes de la vista
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1216, 680));
        setBackground(new Color(240, 248, 255));

        // Panel superior - Título y bienvenida
        panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(240, 248, 255));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        lblTitulo = new JLabel("JUGADORES DISPONIBLES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(70, 130, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JugadorDTO jugadorLocal = controlador.getJugadorLocal();
        lblBienvenida = new JLabel("Bienvenido, " + jugadorLocal.getNombre());
        lblBienvenida.setFont(new Font("Arial", Font.PLAIN, 18));
        lblBienvenida.setForeground(new Color(100, 100, 100));
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(lblBienvenida);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel centro - Tabla de jugadores
        panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(new Color(240, 248, 255));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Crear tabla
        String[] columnas = {"Nombre", "Color", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaJugadores = new JTable(modeloTabla);
        tablaJugadores.setFont(new Font("Arial", Font.PLAIN, 16));
        tablaJugadores.setRowHeight(35);
        tablaJugadores.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tablaJugadores.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaJugadores.getTableHeader().setForeground(Color.WHITE);
        tablaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJugadores.setSelectionBackground(new Color(135, 206, 250));

        scrollPane = new JScrollPane(tablaJugadores);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));

        panelCentro.add(scrollPane, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // Panel inferior - Botones y estado
        panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setBackground(new Color(240, 248, 255));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(new Color(240, 248, 255));

        btnSeleccionar = new JButton("Seleccionar Oponente");
        btnSeleccionar.setFont(new Font("Arial", Font.BOLD, 16));
        btnSeleccionar.setPreferredSize(new Dimension(200, 40));
        btnSeleccionar.setBackground(new Color(70, 130, 180));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setEnabled(false);
        btnSeleccionar.addActionListener(e -> seleccionarOponente());

        btnRefrescar = new JButton("Refrescar Lista");
        btnRefrescar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnRefrescar.setPreferredSize(new Dimension(150, 40));
        btnRefrescar.setBackground(new Color(100, 149, 237));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> controlador.solicitarListaJugadores());

        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnRefrescar);

        // Label de estado
        lblEstado = new JLabel("Cargando jugadores disponibles...");
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 14));
        lblEstado.setForeground(new Color(100, 100, 100));
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInferior.add(panelBotones);
        panelInferior.add(Box.createVerticalStrut(10));
        panelInferior.add(lblEstado);

        add(panelInferior, BorderLayout.SOUTH);

        // Listener para habilitar botón de selección
        tablaJugadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSeleccionar.setEnabled(tablaJugadores.getSelectedRow() != -1);
            }
        });
    }

    private List<JugadorDTO> jugadoresActuales;

    /**
     * Maneja la selección de un oponente
     */
    private void seleccionarOponente() {
        int filaSeleccionada = tablaJugadores.getSelectedRow();
        if (filaSeleccionada == -1 || jugadoresActuales == null || filaSeleccionada >= jugadoresActuales.size()) {
            mostrarError("Por favor, seleccione un jugador válido");
            return;
        }

        JugadorDTO jugadorSeleccionado = jugadoresActuales.get(filaSeleccionada);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Desea invitar a " + jugadorSeleccionado.getNombre() + " a una partida?",
            "Confirmar Invitación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            controlador.seleccionarOponente(jugadorSeleccionado);
            lblEstado.setText("Invitación enviada a " + jugadorSeleccionado.getNombre() + ". Esperando respuesta...");
            lblEstado.setForeground(new Color(100, 149, 237));
        }
    }

    // Implementación de IVistaListaJugadores

    @Override
    public void actualizarListaJugadores(List<JugadorDTO> jugadores) {
        SwingUtilities.invokeLater(() -> {
            this.jugadoresActuales = jugadores;
            modeloTabla.setRowCount(0);

            if (jugadores.isEmpty()) {
                mostrarSinJugadores();
                return;
            }

            for (JugadorDTO jugador : jugadores) {
                String nombre = jugador.getNombre();
                String color = jugador.getColor().equals(Color.RED) ? "Rojo" : "Azul";
                String estado = jugador.isEnPartida() ? "En partida" : "Disponible";
                modeloTabla.addRow(new Object[]{nombre, color, estado});
            }

            lblEstado.setText("Jugadores disponibles: " + jugadores.size());
            lblEstado.setForeground(new Color(0, 150, 0));
        });
    }

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
    public void jugadorSeleccionado(JugadorDTO jugador) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("Oponente seleccionado: " + jugador.getNombre());
            lblEstado.setForeground(new Color(70, 130, 180));
        });
    }

    @Override
    public void mostrarSinJugadores() {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
            lblEstado.setText("No hay jugadores disponibles. Esperando...");
            lblEstado.setForeground(Color.ORANGE);
            btnSeleccionar.setEnabled(false);
        });
    }

    @Override
    public void mostrarInvitacion(SolicitudPartidaDTO solicitud) {
        SwingUtilities.invokeLater(() -> {
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                solicitud.getNombreSolicitante() + " te invita a jugar.\n¿Aceptas?",
                "Invitación de Partida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                controlador.aceptarInvitacion();
            } else {
                controlador.rechazarInvitacion();
            }
        });
    }

    @Override
    public void partidaAceptada(SolicitudPartidaDTO solicitud) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(solicitud.getNombreInvitado() + " aceptó tu invitación!");
            lblEstado.setForeground(new Color(0, 150, 0));
        });
    }

    @Override
    public void partidaRechazada(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.setForeground(Color.ORANGE);
            JOptionPane.showMessageDialog(this, mensaje, "Invitación Rechazada", JOptionPane.WARNING_MESSAGE);
        });
    }

    @Override
    public void partidaIniciada(SolicitudPartidaDTO solicitud) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText("Partida iniciada!");
            lblEstado.setForeground(new Color(0, 150, 0));
            JOptionPane.showMessageDialog(
                this,
                "¡Partida iniciada!\nVs: " +
                    (solicitud.getIdSolicitante().equals(controlador.getJugadorLocal().getId()) ?
                     solicitud.getNombreInvitado() : solicitud.getNombreSolicitante()),
                "¡A jugar!",
                JOptionPane.INFORMATION_MESSAGE
            );
            // TODO: Transicionar a vista de configuración de naves
        });
    }

    /**
     * Limpia recursos al cerrar la vista
     */
    public void limpiar() {
        if (controlador != null) {
            controlador.detener();
        }
    }
}
