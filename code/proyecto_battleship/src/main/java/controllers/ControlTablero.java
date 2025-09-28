package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ControlTablero implements Initializable {

    @FXML
    private GridPane tableroGrid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int numFilas = 10;
        int numColumnas = 10;

        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                StackPane casillaPane = new StackPane();
                Rectangle celda = new Rectangle(40, 40);
                celda.setFill(Color.LIGHTBLUE);
                celda.setStroke(Color.BLACK);

                casillaPane.getChildren().add(celda);

                final int fila = i;
                final int columna = j;

                casillaPane.setOnMouseClicked(event -> {
                    System.out.println("Clic en la casilla mock: (" + fila + ", " + columna + ")");
                    celda.setFill(Color.ORANGE);
                });

                tableroGrid.add(casillaPane, columna, i);
            }
        }
    }
}