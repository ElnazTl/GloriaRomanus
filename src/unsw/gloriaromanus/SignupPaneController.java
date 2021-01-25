package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SignupPaneController extends MenuController {

    @FXML
    private TextField user_name;
    @FXML
    private TextField user_faction;
    @FXML
    private TextArea output_terminal;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

   @FXML

    public void change() throws JsonParseException, JsonMappingException, IOException {
        getParent().nextMenu("unsw.gloriaromanus.SignupPaneController","unsw.gloriaromanus.ActionController");
    }

    public void appendToTerminal(String message) {
        output_terminal.clear();

        output_terminal.appendText(message + "\n");
    }

    @FXML
    public void clickedRegisterButton(ActionEvent e) throws IOException {
        if (user_name.getText().isEmpty()) appendToTerminal("error: name can't be empty");
        if (user_faction.getText().isEmpty()) appendToTerminal("error: faction can't be empty");
        getParent().registerUser(user_name.getText(),user_faction.getText());
        clean();

    }

    @FXML
    public void clickedStartGame(ActionEvent e) throws IOException {
        getParent().startGame();
    }
    private void clean() {
        user_faction.setText("");
        user_name.setText("");
    }
}

