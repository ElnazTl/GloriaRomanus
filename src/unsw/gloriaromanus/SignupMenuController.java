package unsw.gloriaromanus;

// import java.io.IOException;
// import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
import javafx.scene.control.Button;


public class SignupMenuController extends MenuController{
    // @FXML
    // private TextField user_name;
    
    // @FXML
    // private TextField user_faction;

    // @FXML
    // private TextArea output_terminal;

    // // https://stackoverflow.com/a/30171444
    // @FXML
    // private URL location; // has to be called location

    @FXML
    private Button registerButton;

    private startGameScreen startGameScreen;

    @FXML
    void handleRegisterButton(ActionEvent event) {
        startGameScreen.start();
    }

    public void setStartGameScreen(startGameScreen startGameScreen) {
        this.startGameScreen = startGameScreen;
    }

    // public void setUsername(String p) {
    //     user_name.setText(p);
    // }

    // public void setfaction(String p) {
    //     user_faction.setText(p);
    // }

    // public void appendToTerminal(String message) {
    //     output_terminal.appendText(message + "\n");
        
    // }

    // @FXML
    // public void clickeregisterButton(ActionEvent e) throws IOException {
    //     output_terminal.clear();
    //     if(user_name.getText().isEmpty()) appendToTerminal("error: no user name");
    //     if(user_faction.getText().isEmpty()) appendToTerminal("error: no faction");
    //     else {
    //         getParent().registerUser(user_name.getText(),user_faction.getText());
    //     }
    // }
}
