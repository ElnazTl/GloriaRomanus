package unsw.gloriaromanus.Backend;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Player {

    private Faction faction;
    public String username;
    Boolean turn;
    Database database;

   
    public Player(){}
    public Player(String username, Database database) throws IOException {
        this.username = username;
        this.database = database;
        registerPlayer();
        turn = false;

    }

    public Boolean getTurn() {
        return turn;
    }

    /**
     * Player choosing their faction 
     * 
     * @param faction
     */
    public void setDatabase(Database d) {
        this.database = d;
    }
    public void registerPlayer() throws IOException{
        database.addPlayer(this);
    }
    public void chooseFaction(Faction faction) {
        // Faction f = new Faction(faction);
        setFaction(faction);
    }

    private void setFaction(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }

    /**
     * setting the player turn
     */
    public String startTurn() {
        
        for (Player p: database.getPlayers()) {
            if (p.getTurn())return "It's another player's turn";
        }
        turn = true;
        return "now it's your turn";
    }

    public void endTurn() {
        if (!turn) {
            // Not player turn, cant end turn
        }
        faction.endTurn();
        turn = false;
    }


    public int getFactionTreasury() {
        return faction.getTreasury();
    }


    public void moveUnits() {

    }


    public void invade(String ownedProvince, String enemyProvince) {
        faction.invade(ownedProvince, enemyProvince);
    }


    /**
     * Attempts to train a given unit in a given province
     * 
     * @param province Province to train unit in
     * @param unit Unit to train
     * @throws IOException
     */
    public boolean trainUnit(String province, String unit) throws IOException {
        boolean training = faction.trainUnit(province, unit);
        if (training) {
            // Unit is training
            System.out.println("Trained unit successfully");
        } else {
            // Unit training failed
            System.out.println("Could not train unit");
        }
        return training;
    }

    /**
     * if enough gold, buying the required unit for the given province
     * 
     * @param name
     * @param category
     * @param province
     * @return
     */

    
    public String getUnit(String name, String province) throws IOException {
        if (!database.getFactionProvince().get(province).getName().equals(faction.getName())) return "can only get unit for the faction you belong to";
        if (turn) {
            if (!database.addUnit(name,faction.getName(),province)) return "can't add unit";
            return "successfully added the unit";
        }

        return "It's not your turn";
    }


    /*
     * given the unit and from and to if the provinces are adjacent the unit will be
     * moved
     * 
     * @param u
     * @param from
     * @param to
     * @return
     */

    public boolean moveUnits(String from, String to) throws IOException {
        boolean move = faction.moveUnits(from, to);
        if (move) {
            // Unit moved successfully
            System.out.println("Move units successfully");
        } else {
            // Unit could not move
            System.out.println("Could not move units");
        }
        return move;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "Player: " + username;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Player p = (Player)obj;

        return username.equals(p.getUsername());
    }
}
