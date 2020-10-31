package unsw.gloriaromanus.Backend;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Player {

    private String faction;
    public String username;
    Boolean turn;

   
    public Player(){}
    public Player(String username) {
        this.username = username;

    }

    
    public List<Faction> getAvailableFactions(Database db) {
        return db.getAvailableFactions();
    }


    private void setFaction(String faction) {
        this.faction = faction;
    }

    public String getFaction() {
        return faction;
    }

    /**
     * setting the player turn
     */
    public void startTurn() {
        turn = true;
    }

    public void endTurn() {
        db.endTurn();
        turn = false;
    }


    public void invade(String ownedProvince, String enemyProvince) {
        faction.invade(ownedProvince, enemyProvince);
    }

    /**
     * if enough gold, buying the required unit for the given province
     * 
     * @param name
     * @param category
     * @param province
     * @return
     */

    // implementing turn ?
    public String getUnit(String name, String province) throws IOException {
        if (turn) {
            return database.addUnit(name, province);
        }

        return "It's not your turn";
    }

    /**
     * given the unit and from and to if the provinces are adjacent the unit will be
     * moved
     * 
     * @param u
     * @param from
     * @param to
     * @return
     */

    public String moveTroop(String u, String from, String to) throws IOException {

        Province f = new Province(from, database);
        Province t = new Province(to, database);

        return f.moveTroopTo(t, u);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
