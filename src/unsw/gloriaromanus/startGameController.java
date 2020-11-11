package unsw.gloriaromanus;

// import java.io.IOException;
// import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import unsw.gloriaromanus.*;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;

public class startGameController {
    @FXML
    private Button backButton;

    private signupScreen signupScreen;

    private GloriaRomanusApplication gra;

    @FXML
    public void handleBackButton(ActionEvent event) {
        signupScreen.start();
    }

    public void setSignupScreen(signupScreen signupScreen) {
        this.signupScreen = signupScreen;
    }

    public void setGra(GloriaRomanusApplication gra) {
        this.gra = gra;
    }
    //going to the map page and asking for the user faction
    @FXML
    public void clickedNewGame(){

        gra.start();

    }

    
}
