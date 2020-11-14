package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class startGameScreen {
    private Stage stage;
    private String title;
    private startGameController controller;

    private Scene scene;

    public startGameScreen(Stage stage) throws IOException {
        this.stage = stage;
        title = "Start Screen";

        controller = new startGameController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("startGame.fxml"));
        // controller= loader.getController();
        loader.setController(controller);

        // load into a Parent node called root
        Parent root = loader.load();
        scene = new Scene(root, 800, 500);
    }

    public void start() {
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public startGameController getController() {
        return controller;
    }
}
