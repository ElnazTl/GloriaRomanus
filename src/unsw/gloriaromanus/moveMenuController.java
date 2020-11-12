
package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class moveMenuController extends MenuController {
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TextArea output_terminal;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    @FXML
    public void setFromProvince(String p) {
        from.setText(p);
    }
    @FXML
    public void setToProvince(String p) {
        to.setText(p);
    }

    public void appendToTerminal(String message) {
        output_terminal.appendText(message + "\n");
    }

   

    @FXML
    public void clickedMoveButton() throws IOException {
        getParent().MoveUnit(to.getText(), from.getText(), "soldier");
    }
    @FXML
    public void clickedBackButton(ActionEvent e) throws IOException {
        getParent().nextMenu("unsw.gloriaromanus.moveMenuController", "unsw.gloriaromanus.ActionController");
        // getParent().trainUnit("soldier");
        
    }
    @FXML
    public void endTurn(ActionEvent e) throws IOException {
        getParent().endTurn();
    }
    
}
