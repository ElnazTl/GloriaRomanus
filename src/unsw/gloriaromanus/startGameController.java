package unsw.gloriaromanus;

// import java.io.IOException;
// import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;

public class startGameController {
    @FXML
    private Button backButton;

    private signupScreen signupScreen;

    @FXML
    public void handleBackButton(ActionEvent event) {
        signupScreen.start();
    }

    public void setSignupScreen(signupScreen signupScreen) {
        this.signupScreen = signupScreen;
    }

    
}
