package unsw.gloriaromanus.Backend;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player {

    private Faction faction;
    public String username;

   
    /**
     * Default constructor used for deserialisation
     */
    public Player(){}

    /**
     * Initialises a player with specified name
     * @param username
     */
    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Selects a province with the given name
     * @param name
     */
    public void selectProvince(String name) {
        faction.selectProvince(name);
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }


    public Faction getFaction() {
        return faction;
    }


    /**
     * Ends the turn of the player
     */
    public void endTurn() {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        faction.endTurn();
    }


    /**
     * Returns if it is the players turn or not
     * @return
     */
    @JsonIgnore
    public boolean isTurn() {
        return faction.isTurn();
    }


    /**
     * Attempts to invade the specified enemy province
     * from the selected province
     * @param enemyProvince
     */
    public int invade(String enemyProvince) {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return -1;
        }
        return faction.invade(enemyProvince);
    }


    /**
     * Attempts to train a given unit in a given province
     * 
     * @param unit Unit to train
     * @throws IOException
     */
    public void trainUnit(String unit) throws IOException {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        boolean training = faction.trainUnit(unit);
        if (training) {
            // Unit is training
            System.out.println("Trained unit successfully");
        } else {
            // Unit training failed
            System.out.println("Could not train unit");
        }
    }


    /**
     * given the unit and from and to if the provinces are adjacent the unit will be
     * moved
     * 
     * @param to
     * @return
     */
    public boolean moveUnits(String to) throws IOException {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return false;
        }
        boolean move = faction.moveUnits(to);
        if (move) {
            // Unit moved successfully
            System.out.println("Move units successfully");
        } else {
            // Unit could not move
            System.out.println("Could not move units");
        }
        return move;
    }


    /**
     * Selects unit with given id in selected province
     * @param unitID
     */
    public void selectUnit(Long unitID) {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        faction.selectUnit(unitID);
    }

    /**
     * Returns a list of all provinces owned by the faction
     * @return
     */
    @JsonIgnore
    public String getProvinces() {
        return faction.getProvinces().toString();
    }

    /**
     * Returns the string representation of 
     * province specified
     * @param name
     * @return
     */
    @JsonIgnore
    public String getProvinceState(String name) {
        return faction.getProvinceState(name);
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
