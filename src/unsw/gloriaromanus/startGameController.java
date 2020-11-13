package unsw.gloriaromanus;

// import java.io.IOException;
// import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import unsw.gloriaromanus.*;

import java.io.FileWriter;
import java.io.IOException;
 
import org.json.JSONObject;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
public class startGameController {
    @FXML
    private Button backButton;

    @FXML
    private ImageView titleImage;

    private signupScreen signupScreen;

    private GloriaRomanusApplication gra;

    private GloriaRomanusController children;


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




    public void setChild(GloriaRomanusController child) {
        System.out.println("hello we set the child");
        this.children = child;
    }
    /**
     * start the game with action menu page
     * and jumping straightly from game page to the action page
     * load game: db.load
     */
    @FXML
    public void clickedLoadGame() {
        System.out.println("loading");
        gra.start();


    }
    

}
