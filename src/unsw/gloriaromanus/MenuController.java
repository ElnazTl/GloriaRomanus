package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class MenuController {
    private GloriaRomanusController parent;
    // private GloriaRomanusController child;


    public void setParent(GloriaRomanusController parent) {
        if (parent == null){
            System.out.println("GOT NULL");
        }
        this.parent = parent;
        
    } 
    // public void setchild(startGameController parent, GloriaRomanusController child) {
    //     if (parent == null){
    //         System.out.println("GOT NULL");
    //     }
    //     parent.setChild(child);
        
    // } 

    public GloriaRomanusController getParent(){
        return parent;
    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        parent.switchMenu();
    }

}
