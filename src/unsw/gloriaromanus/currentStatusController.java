package unsw.gloriaromanus;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

// import java.io.IOException;
// import java.net.URL;

// import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class currentStatusController extends MenuController{

    @FXML
    private Button endGameButton;

    @FXML
    private TextField userNameBox;

    @FXML
    private TextField currentYearBox;

    @FXML
    private TextField currentBalanceBox;

    @FXML
    private TextField conqueredBox;


    @FXML
    public void turnInitialize() {
        currentYearBox.setText("1");
    }

    @FXML
    public void balanceInitialize() {
        currentBalanceBox.setText("0");
    }

    @FXML
    public void conqueredInitialize() {
        conqueredBox.setText("1");
    }
    

    @FXML
    public void setName(String name) {
        userNameBox.setText(name);

    }

    @FXML
    public void setYear(String year) {
        currentYearBox.setText(year);
    }
    @FXML
    public void endTurn() throws JsonParseException, JsonMappingException, IOException {
        getParent().endTurn();
    }
    @FXML 
    public void saveGame() {
        System.out.println("end");
    }



    // @FXML
    // public void clickedEndGame(ActionEvent e) throws IOException {
    //     getParent().endTurn();
    //     getParent().nextMenu();
    // }

}

