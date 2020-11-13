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

public class getUnitController extends MenuController{
    ObservableList<String> getUnits = FXCollections.observableArrayList("soldier","horseArcher","hoplite","pikemen","romanLegionary","berserker","javelinSkirmisher","elephant","druid");
    
    @FXML
    private TextField province;
  
    @FXML
    private TextArea output_terminal;
    
    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    @FXML
    private ChoiceBox<String> getUnitChoice;

    @FXML
    private void initialize(){
        getUnitChoice.setValue("soldier");
        getUnitChoice.setItems(getUnits);
    };
    
    @FXML
    public void appendToTerminal(String message) {
        output_terminal.clear();
        output_terminal.appendText(message + "\n");
    }

   

    @FXML
    public void clickedGetUnit() throws IOException {
        getParent().getUnit(province.getText(), "soldier");
    }
    @FXML
    public void clickedBackButton(ActionEvent e) throws IOException {
        getParent().clean();

        getParent().nextMenu("unsw.gloriaromanus.getUnitController", "unsw.gloriaromanus.ActionController");
        // getParent().trainUnit("soldier");
        
    }

    @FXML
    public void setUnit(String p) {
        province.setText(p);
    }
   
}



