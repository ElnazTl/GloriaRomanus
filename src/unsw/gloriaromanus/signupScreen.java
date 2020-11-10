package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class signupScreen {

    private Stage stage;
    private String title;
    private SignupMenuController controller;
    private Scene scene;

    public signupScreen(Stage stage) throws IOException {
        this.stage = stage;
        title = "Start Screen";

        controller = new SignupMenuController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
        loader.setController(controller);

        // load into a Parent node called root
        Parent root = loader.load();
        scene = new Scene(root, 500, 300);
    }

    public void start() {
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public SignupMenuController getController() {
        return controller;
    }
}
