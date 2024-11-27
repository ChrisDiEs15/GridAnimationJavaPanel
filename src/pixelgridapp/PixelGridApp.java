package pixelgridapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PixelGridApp extends JFrame {
    private static final int ROWS = 10; // Número de filas en la cuadrícula
    private static final int COLS = 20; // Número de columnas en la cuadrícula
    private final PixelPanel[][] grid = new PixelPanel[ROWS][COLS]; // Matriz de píxeles
    private boolean running = false; // Controla si la animación está activa
    private Thread animationThread; // Referencia al hilo de animación

    private final JTextField speedField; // Campo para ingresar la velocidad
    private final JButton startButton; // Botón de inicio
    private final JButton stopButton;  // Botón de detener
    private final JButton clearButton;// Botón para limpiar la cuadrícula

    public PixelGridApp() {
        setTitle("Cuadrícula Animada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de cuadrícula
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = new PixelPanel(); // Inicializa cada celda de la cuadrícula
                gridPanel.add(grid[i][j]); // Agrega cada celda al panel
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        // Panel inferior para controles
        JPanel controlPanel = new JPanel();
        JLabel speedLabel = new JLabel("Velocidad (ms):");
        speedField = new JTextField("1000", 5); // Velocidad inicial de 1000 ms
        startButton = new JButton("Iniciar");
        stopButton = new JButton("Detener");
        clearButton = new JButton("Limpiar");

         // Agrega los componentes de control al panel
        controlPanel.add(speedLabel);
        controlPanel.add(speedField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Acción del botón de inicio
        startButton.addActionListener(e -> startAnimation());

        // Acción del botón de detener
        stopButton.addActionListener(e -> stopAnimation());
        
        //Accion del boton de limpiar
        clearButton.addActionListener(e -> limparPanel());
        

        // Etiqueta con tu nombre
        JLabel nameLabel = new JLabel("Diaz Espinosa Christian Alejandro");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);// Centra la etiqueta
        add(nameLabel, BorderLayout.NORTH);

        pack();// Ajusta el tamaño de la ventana al contenido
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);// Hace la ventana visible
    }

    // Método para iniciar la animación
    public void startAnimation() {
        if (animationThread != null && animationThread.isAlive()) {
            return; // Si ya hay un hilo ejecutándose, no hacer nada
        }

        running = true; // Activar la animación
        animationThread = new Thread(() -> {
            while (running) {
                shiftRight(); // Método para mover los píxeles
                try {
                    int velocidad = Integer.parseInt(speedField.getText());// Obtiene la velocidad
                    Thread.sleep(velocidad); // Pausa basada en la velocidad
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Detener el hilo correctamente
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Velocidad inválida. Por favor ingresa un número.");
                    running = false; // Detener si la velocidad no es válida
                }
            }
        });
        animationThread.start(); // Inicia el hilo
    }

    // Método para detener la animación
    public void stopAnimation() {
        running = false; // Detener la animación
        if (animationThread != null) {
            animationThread.interrupt(); // Interrumpir el hilo
        }
    }

    // Método para mover los píxeles hacia la derecha
    private void shiftRight() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < ROWS; i++) {
                Color lastColor = grid[i][COLS - 1].getBackground();// Almacena el último color de la fila
                for (int j = COLS - 1; j > 0; j--) {
                    grid[i][j].setBackground(grid[i][j - 1].getBackground());// Mueve el color a la derecha
                }
                grid[i][0].setBackground(lastColor); // Coloca el último color al inicio de la fila
            }
        });
    }
    
    //Metodo para limpiar el panel
    
    private void limparPanel() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j].setBackground(Color.WHITE); // Cambia el fondo a blanco
                grid[i][j].setActive(false);           // Resetea el estado del píxel a inactivo
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PixelGridApp::new);// Inicia la aplicación en el hilo de despacho de eventos
    }
}

// Clase PixelPanel que representa un píxel en la cuadrícula
class PixelPanel extends JPanel {
    private static final Color[] COLORS = {
        Color.WHITE,    // Blanco (inactivo)
        Color.BLACK,    // Negro
        Color.RED,      // Rojo
        Color.GREEN,    // Verde
        Color.BLUE,     // Azul
        Color.YELLOW,   // Amarillo
        Color.ORANGE,   // Naranja
        Color.CYAN      // Cian
    };
    private int colorIndex = 0; // Índice del color actual en el arreglo COLORS

    public PixelPanel() {
        setBackground(COLORS[colorIndex]); // Inicialmente blanco (COLORS[0])
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Evento de clic para cambiar el color
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleColor(); // Cambiar al siguiente color
            }
        });
    }

    // Cambia al siguiente color en el arreglo
    public void toggleColor() {
        colorIndex = (colorIndex + 1) % COLORS.length; // Avanza al siguiente color
        setBackground(COLORS[colorIndex]);            // Cambia el color de fondo
    }

    // Verifica si el píxel está activo (no es blanco)
    public boolean isActive() {
        return colorIndex != 0; // El color activo es cualquier cosa que no sea COLORS[0]
    }

    // Establece el estado del píxel (blanco o negro)
    public void setActive(boolean active) {
        colorIndex = active ? 1 : 0; // Negro si activo, blanco si inactivo
        setBackground(COLORS[colorIndex]);
    }
}
