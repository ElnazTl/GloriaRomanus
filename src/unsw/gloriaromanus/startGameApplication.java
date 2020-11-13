package unsw.gloriaromanus;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class startGameApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        signupScreen signupScreen = new signupScreen(primaryStage);
        startGameScreen startGameScreen = new startGameScreen(primaryStage);
        GloriaRomanusApplication gloriaRomanusApplication = new GloriaRomanusApplication(primaryStage);
        
        // Both controllers need to know about the other screen.

        signupScreen.getController().setStartGameScreen(startGameScreen);
        // startGameScreen.getController().setSignupScreen(signupScreen);
        startGameScreen.getController().setGra(gloriaRomanusApplication);

        startGameScreen.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
