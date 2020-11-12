package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ActionController extends MenuController {
    @FXML
    public void trainUnit() {
        System.out.println("x");
    }
    @FXML
    public void moveTroop() {
        
        System.out.println("x");
    }
    @FXML
    public void invade() throws JsonParseException, JsonMappingException, IOException {
        getParent().nextMenu("unsw.gloriaromanus.ActionController", "unsw.gloriaromanus.InvasionMenuController");
    }
    
}
