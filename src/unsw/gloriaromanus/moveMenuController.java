
package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class moveMenuController extends MenuController {
    ObservableList<String> unitsMove = FXCollections.observableArrayList("");

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
    private ChoiceBox<String> unitMoveChoice;

    @FXML
    private void initialize(){
        
        unitMoveChoice.setItems(unitsMove);
        unitMoveChoice.setOnMouseClicked(e -> {
            unitsMove = FXCollections.observableArrayList(getParent().getAvailableUnit(from.getText()));
            unitMoveChoice.setItems(unitsMove);

        });
    }

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
        getParent().clean();
        clean();
    }
    @FXML
    public void clickedBackButton(ActionEvent e) throws IOException {
        getParent().clean();
        clean();
        getParent().nextMenu("unsw.gloriaromanus.moveMenuController", "unsw.gloriaromanus.ActionController");
        // getParent().trainUnit("soldier");
        
    }
    @FXML
    public void endTurn(ActionEvent e) throws IOException {
        getParent().endTurn();
    }
    private void clean() {
        from.setText("");
        to.setText("");
    }
    
}
