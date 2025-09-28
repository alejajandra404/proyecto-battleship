package equipo_1.proyecto_battleship;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProyectoBattleship extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/equipo_1/proyecto_battleship/views/Tablero.fxml"));
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Battleship FX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * El método main es necesario para que Maven y los IDEs puedan lanzar
     * la aplicación JavaFX correctamente como un artefacto ejecutable.
     * @param args los argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}