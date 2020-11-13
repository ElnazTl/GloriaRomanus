package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;

public class InvasionMenuController extends MenuController {
    ObservableList<String> units 
    = FXCollections.observableArrayList("");

    @FXML
    private TextField invading_province;
    @FXML
    private TextField opponent_province;
    @FXML
    private TextArea output_terminal;

    @FXML
    private ChoiceBox<String> unitChoice;

    @FXML
    private void initialize(){


        unitChoice.setItems(units);
        unitChoice.setOnMouseClicked(e -> {
            units = FXCollections.observableArrayList(getParent().getAvailableUnit(invading_province.getText()));
            unitChoice.setItems(units);

        });
        System.out.println(units);
       

    }

    
    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    @FXML
    public void setInvadingProvince(String p) {

        invading_province.setText(p);
            // box.setItem;
    }
    @FXML
    public void setOpponentProvince(String p) {
        opponent_province.setText(p);
    }

    public void appendToTerminal(String message) {
        output_terminal.clear();
        output_terminal.appendText(message + "\n");
    }

   

    @FXML
    public void clickedInvadeButton(ActionEvent e) throws IOException {
        getParent().clickedInvadeButton(invading_province.getText(),opponent_province.getText(),unitChoice.getValue());
        getParent().clean();
        clean();
    }
    @FXML
    public void clickedBackButton(ActionEvent e) throws IOException {
        clean();
        getParent().clean();
        getParent().nextMenu("unsw.gloriaromanus.InvasionMenuController", "unsw.gloriaromanus.ActionController");
        
        // getParent().trainUnit("soldier");
        
    }
    @FXML
    public void endTurn(ActionEvent e) throws IOException {
        getParent().endTurn();
    }
    private void clean() {
        invading_province.setText("");
        opponent_province.setText("");
    }
}
